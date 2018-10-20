package com.example.poojan.ezcommuter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountSetting extends AppCompatActivity {

    TextView accName, accType, accEmail;
    DatabaseReference dbref;
    FirebaseAuth auth;
    public ProgressDialog dialog;
    AlertDialog.Builder AlertName;
    EditText edittext;
    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        toolbar = findViewById(R.id.accout_setting);
        toolbar.setTitle("Account Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(AccountSetting.this);
        dialog.setMessage("Updating please wait...");

        auth = FirebaseAuth.getInstance();
        accName = findViewById(R.id.accName);
        accType = findViewById(R.id.accType);
        accEmail = findViewById(R.id.email);

        dbref = FirebaseDatabase.getInstance().getReference().child("user_details").child(auth.getUid());
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                accName.setText(user.getUserName().toString());
                accEmail.setText(user.getUserEmail().toString());
                accType.setText(user.getUserType().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        accName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                dialog.show();
                AlertName = new AlertDialog.Builder(AccountSetting.this);
                edittext = new EditText(AccountSetting.this);
                edittext.setText(accName.getText().toString());
                AlertName.setTitle("Change User Name");
                //alert.setMessage("Enter Your Title");
                AlertName.setView(edittext);
                AlertName.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        final String newName = String.valueOf(edittext.getText());
                        DatabaseReference accountref = FirebaseDatabase.getInstance().getReference()
                                .child("user_details")
                                .child(auth.getUid())
                                .child("userName");

                        accountref.setValue(newName);
                        finish();
                        startActivity(getIntent());
                        dialog.dismiss();
                    }
                });

                AlertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.dismiss();
                    }
                });
                AlertName.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialog.dismiss();
                    }
                });
                AlertName.show();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(AccountSetting.this,Home.class);
        startActivity(i);
        finish();

    }
}
