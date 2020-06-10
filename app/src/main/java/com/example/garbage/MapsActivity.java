package com.example.garbage;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng latLng;
    Marker marker;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView name, phone;
    ImageView profile;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_track);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        name = findViewById(R.id.nameid);
        phone = findViewById(R.id.phone);
        profile = findViewById(R.id.propic);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Location").child(drawer.WardNo);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


    }


    public void getPic(String id){
        storageReference = FirebaseStorage.getInstance().getReference().child("Cleaner dps/"+ id);
        try {
            final File files = File.createTempFile("images", "jpg");
            storageReference.getFile(files)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(files.getAbsolutePath());
                            profile.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Toast.makeText(MapsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        databaseReference.addValueEventListener(new ValueEventListener() {
            Double Latitude, Longitude;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (marker != null) {
                    marker.remove();
                }
                if(!dataSnapshot.exists()){
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Tracking Unavailable")
                            .setMessage("The tracking information is not updated yet. Please try again later.")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else {
                    //Toast.makeText(MapsActivity.this, "Yes YEs ", Toast.LENGTH_SHORT).show();
                        try {
                            name.setText(dataSnapshot.child("Name").getValue().toString());
                            phone.setText(dataSnapshot.child("Mobile").getValue().toString());
                            id = dataSnapshot.child("ID").getValue().toString();
                            getPic(id);
                            latLng = new LatLng((Double) dataSnapshot.child("Latitude").getValue(), (Double) dataSnapshot.child("Longitude").getValue());
                            Latitude = Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString());
                            Longitude = Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString());
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("My Current Position"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 2000, null);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                    // Toast.makeText(getApplicationContext(), Latitude + " " +Longitude, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

}
