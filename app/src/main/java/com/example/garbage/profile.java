package com.example.garbage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static com.example.garbage.drawer.databaseReference1;
import static com.example.garbage.drawer.storageReference;

public class profile extends AppCompatActivity {
    EditText name, email, houseNo, wardNo, password;
    ImageButton edit, choose;
    ImageView profile;
    Button done, upload;
    Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        edit = findViewById(R.id.edit);
        done = findViewById(R.id.doneButton);
        name = findViewById(R.id.nameid);
        email = findViewById(R.id.phone);
        houseNo = findViewById(R.id.houseid);
        wardNo = findViewById(R.id.wardid);
            password = findViewById(R.id.password);
            profile = findViewById(R.id.propic);
        upload = findViewById(R.id.upload);
        choose = findViewById(R.id.choose);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                wardNo.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));
                houseNo.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));

                name.setEnabled(true);
                wardNo.setEnabled(true);
                houseNo.setEnabled(true);

                done.setVisibility(View.VISIBLE);
                edit.setVisibility(View.INVISIBLE);
                choose.setVisibility(View.VISIBLE);
                upload.setVisibility(View.VISIBLE);
                drawer.flag = 0;
            }
        });

        email.setText(drawer.Email);
        name.setText(drawer.fullName);
        houseNo.setText(drawer.houseNO);
        wardNo.setText(drawer.WardNo);
        password.setText(drawer.pw);
        Bitmap bitmap = BitmapFactory.decodeFile(drawer.localFile.getAbsolutePath());
        if(bitmap!=null)
            profile.setImageBitmap(bitmap);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference1.child("name").setValue(name.getText().toString().trim());
                databaseReference1.child("houseNo").setValue(houseNo.getText().toString().trim());
                databaseReference1.child("email").setValue(email.getText().toString().trim());
                databaseReference1.child("password").setValue(password.getText().toString().trim());
                databaseReference1.child("wardNo").setValue(wardNo.getText().toString().trim());
                Toast.makeText(profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), drawer.class));
                finish();
            }
        });


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            filepath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(filepath != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            storageReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(profile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+ (int)progress+"%");
                }
            });
        }
    }
}
