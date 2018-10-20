package com.example.poojan.ezcommuter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Fine extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText user;
    private EditText amt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine);
        Spinner spinner = (Spinner) findViewById(R.id.finetype);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fine_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        amt = findViewById(R.id.amt);
        user = findViewById(R.id.email);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query r = ref.child("FineType").orderByChild("type").equalTo(spinner.getSelectedItem().toString());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                amt.setText(dataSnapshot.child("amt").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fineclass fc = new fineclass( mAuth.getCurrentUser().getEmail(),user.getText().toString(),spinner.getSelectedItem().toString(),amt.getText().toString());

        ref.child("Fine").push().setValue(fc);
    }
}
