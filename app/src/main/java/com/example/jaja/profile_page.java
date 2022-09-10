package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import adapter_and_fragmets.Users;

public class profile_page extends AppCompatActivity {

    private LinearLayout backBtn;
    private ImageView iv_settings, iv_addPhoto, iv_userPhoto;
    private TextView tv_userName;

    public static final int GET_FROM_GALLERY = 3;

    private Uri imageUri;
    private String userID, tempImageName;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userStorage = FirebaseStorage.getInstance().getReference("Users").child(userID);

        setRef();
        generateData();
        clickListeners();
    }

    private void generateData() {

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fullName = userProfile.getFullName();
                    String sp_imageUrl = userProfile.getImageUrl();
                    tempImageName = userProfile.getImageName();

                    tv_userName.setText(sp_fullName);

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_userPhoto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clickListeners() {
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_page.this, settings_page.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_page.this, homepage.class);
                startActivity(intent);
            }
        });

        iv_addPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                boolean pick = true;
                PickImage();
//                if (pick){
//                    if(!checkCameraPermission()){
//                        requestCameraPermission();
//                    }else
//                        PickImage();
//
//                }else{
//                    if(!checkStoragePermission()){
//                        requestStoragePermission();
//                    }else
//                        PickImage();
//                }
            }
        });
    }

    private void PickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),GET_FROM_GALLERY);

        //CropImage.activity().start(this);
    }

    private void setRef() {
        iv_addPhoto = findViewById(R.id.iv_addPhoto);
        iv_settings = findViewById(R.id.iv_settings);
        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        backBtn = findViewById(R.id.backBtn);
        tv_userName = findViewById(R.id.tv_userName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();

            Picasso.get().load(imageUri)
                    .into(iv_userPhoto);

            new AlertDialog.Builder(profile_page.this)
                    .setTitle("Upload Photo")
                    .setMessage("Upload this photo as your profile picture?")
                    .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(profile_page.this, "Updating profile", Toast.LENGTH_SHORT).show();
                            updateProject();

                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            Intent intent = new Intent(profile_page.this, profile_page.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void updateProject() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Photo...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference fileReference = userStorage.child(imageUri.getLastPathSegment());

        String imageName = imageUri.getLastPathSegment();

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String imageURL = uri.toString();


                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("imageName", imageName);
                                hashMap.put("imageUrl", imageURL);


                                userDatabase.child(userID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                        if(!tempImageName.isEmpty()){
                                            StorageReference imageRef = userStorage.child(tempImageName);
                                            imageRef.delete();
                                        }

                                        progressDialog.dismiss();
                                        Intent intent = new Intent(profile_page.this, profile_page.class);
                                        startActivity(intent);

                                        Toast.makeText(profile_page.this, "Profile is updated", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile_page.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    // validate permissions
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }
}