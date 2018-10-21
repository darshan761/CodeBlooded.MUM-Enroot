package com.example.poojan.ezcommuter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AccountSetting extends AppCompatActivity {

    TextView accName, accType, accEmail;
    ImageView userImg;
    DatabaseReference dbref;
    FirebaseAuth auth;
    public ProgressDialog dialog;
    AlertDialog.Builder AlertName;
    EditText edittext;
    Bitmap compressed;
    private DatabaseReference accountref, db;
    private StorageReference mStorage;
    android.support.v7.widget.Toolbar toolbar;
    private static final int GALLERY_INTETN=2;
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
        userImg = findViewById(R.id.userimg);

        mStorage = FirebaseStorage.getInstance().getReference();
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

        db = FirebaseDatabase.getInstance().getReference().child("user_details").child(auth.getUid()).child("userImgUrl");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Glide.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).apply(new RequestOptions().circleCrop()).into(userImg);
                }
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

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintetnt =new Intent(Intent.ACTION_PICK);
                mintetnt.setType("image/*");
                startActivityForResult(mintetnt,GALLERY_INTETN);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == GALLERY_INTETN && resultCode==RESULT_OK) {
            dialog.setMessage("Uploading");
            dialog.show();
            Uri uri= data.getData();

            StorageReference filepath= mStorage.child("user_profile_pic").child(uri.getLastPathSegment());
            try
            {
                compressed = MediaStore.Images.Media.getBitmap(AccountSetting.this.getContentResolver(), uri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressed.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] cimg = baos.toByteArray();
            filepath.putBytes(cimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri path= taskSnapshot.getDownloadUrl();
                    accountref = FirebaseDatabase.getInstance().getReference().child("user_details").child(auth.getUid());
                    accountref.child("userImgUrl").setValue(String.valueOf(path));
                    Toast.makeText(AccountSetting.this, "Profile Changed", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());
                    dialog.dismiss();
                }
            });
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AccountSetting.this,Home.class);
        startActivity(i);
        finish();

    }
}
