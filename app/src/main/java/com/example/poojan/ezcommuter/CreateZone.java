package com.example.poojan.ezcommuter;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.loopj.android.http.*;

import org.json.*;

import cz.msebera.android.httpclient.Header;

public class CreateZone extends AppCompatActivity /*implements View.OnClickListener*/ {
    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView image;
    private TextView capture;
    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;
    private FirebaseAuth auth;
    private ProgressDialog mProgress;

    RadioGroup rg;
    RadioButton rb;
    String st1, st2, st3;
    byte[] dataBAOS;

    DatabaseReference databaseReference;
    private EditText ztitle, zdesc, zsol;

    public String zoneImageURI = null;

    Button saveData;

    public String Lat="abc" , Logg="bcd";

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_zone);

        rg = (RadioGroup)findViewById(R.id.RGDonatecategory);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //Hitting API

        Toast.makeText(getApplicationContext(), "Sarah is Dumb!", Toast.LENGTH_SHORT).show();

        String url = "http://172.16.2.4:8000/commuters_app/get_preds";
        AsyncHttpClient client = new AsyncHttpClient();
        File myFile = new File("C:/Users/Chinmay Rane/Desktop/dog.jpg");
        RequestParams params = new RequestParams();
        params.setForceMultipartEntityContentType(true);
        //params.put("q", "Sarah is dumb!");
        try {
            params.put("dog_image", myFile);
        } catch(FileNotFoundException e) {}
        //params.put("rsz", "8");

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here

                Toast.makeText(getApplicationContext(), "Connection Successful!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                Toast.makeText(getApplicationContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Zones");
        //ztitle = (EditText) findViewById(R.id.editTextTitle);
        zdesc = (EditText) findViewById(R.id.editTextDescription);
        zsol = (EditText) findViewById(R.id.editTextSolution);
        auth = FirebaseAuth.getInstance();
        image = findViewById(R.id.picture);
        mProgress = new ProgressDialog(this);
        capture = findViewById(R.id.capture);
        //saveData = findViewById(R.id.save_data);
        //saveData.setOnClickListener(this);

        //findViewById(R.id.save_local).setOnClickListener(saveGallery);
        findViewById(R.id.save_cloud).setOnClickListener(saveCloud);

        firebaseStorage = FirebaseStorage.getInstance();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            dataBAOS = baos.toByteArray();
            image.setImageBitmap(imageBitmap);
        }
    }

    //save captured picture on cloud storage
    private View.OnClickListener saveCloud = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int radioId = rg.getCheckedRadioButtonId();
            rb=findViewById(radioId);


            st2 = rb.getText().toString();

            final String ZoneTitle = st2;
            final String ZoneData = zdesc.getText().toString().trim();
            final String ZoneSolution = zsol.getText().toString().trim();
            //String ZoneStatus = ztitle.getText().toString().trim();
            final String ZoneImage = zoneImageURI;


            if (TextUtils.isEmpty(ZoneData)) {
                Toast.makeText(CreateZone.this, "Please Enter The Zone Description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(ZoneSolution)) {
                Toast.makeText(CreateZone.this, "Please Enter The Zone Solution", Toast.LENGTH_SHORT).show();
                return;
            }
            /*if (TextUtils.isEmpty(ZoneImage)) {
                Toast.makeText(CreateZone.this, "Please Capture The Image First", Toast.LENGTH_SHORT).show();
                return;
            }*/
            if(dataBAOS.equals("")){
                Toast.makeText(CreateZone.this, "Please Capture The Image First", Toast.LENGTH_SHORT).show();
                return;
            }

        /*if (TextUtils.isEmpty(ZoneStatus)) {
            Toast.makeText(this, "Please Enter The Zone Status", Toast.LENGTH_SHORT).show();
            return;
        }*/

            Log.d("bataBAOS0", String.valueOf(dataBAOS[0]));
            mProgress.setMessage("Uploading your Status...");
            mProgress.show();
            StorageReference mStorage = FirebaseStorage.getInstance().getReference()
                    .child(auth.getUid())
                    .child("" + new Date().getTime());
            UploadTask uploadTask = mStorage.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Sending failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    zoneImageURI = taskSnapshot.getDownloadUrl().toString();
                    Log.d("imageUri",zoneImageURI);
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();

                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        getLocation();
                        final String ZoneLat = Lat;
                        final String ZoneLong = Logg;
                        String ZoneKey = databaseReference.push().getKey();
                        Zone zn = new Zone(auth.getUid()+ new Random().nextInt(1000), ZoneTitle, ZoneData, ZoneSolution,
                                ZoneLat, ZoneLong,0,0 , zoneImageURI);
                        databaseReference.child(ZoneKey).setValue(zn);
                        Toast.makeText(CreateZone.this, " All The Details Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateZone.this, Home.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });
            //addToCloudStorage();
        }
    };


    /*private void addToCloudStorage() {
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
                //  Log.e(TAG, "Failed to upload picture to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadURI = uri;
                        zoneImageURI = downloadURI.toString();
                    }
                });
                Toast.makeText(CreateZone.this, "Image has been uploaded to cloud storage", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void checkButton(View view)
    {
        int radioId = rg.getCheckedRadioButtonId();
        rb=findViewById(radioId);

        Toast.makeText(this, "SELECTED CATEGORY IS :"+rb.getText(),Toast.LENGTH_SHORT).show();
    }

    private void addDataZone() {

    }

    /*@Override
    public void onClick(View view) {
        if (view == saveData) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            }
            addDataZone();
        }
    }*/

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(CreateZone.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (CreateZone.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CreateZone.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;



            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}