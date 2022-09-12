package com.example.jaja;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import adapter_and_fragmets.AdapterRecordingsItem;
import adapter_and_fragmets.AudioRecorder;
import adapter_and_fragmets.Recordings;
import adapter_and_fragmets.Users;

public class profile_page extends AppCompatActivity {

    private AudioRecorder audioRecorder;
    private File recordFile;
    RecordView recordView;
    RecordButton recordButton;
    private String mFileName = null;

    private RecyclerView rv_myRecordings;
    private LinearLayout backBtn;
    private ImageView iv_settings, iv_addPhoto, iv_userPhoto;
    private TextView tv_userName;

    public static final int GET_FROM_GALLERY = 3;

    private Uri imageUri;
    private String userID, tempImageName, audioName;
    private ArrayList<Recordings> arrRecordings;
    private AdapterRecordingsItem adapterRecordingsItem;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, audioDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        audioRecorder = new AudioRecorder();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        audioDatabase = FirebaseDatabase.getInstance().getReference("Audio Recordings");
        userStorage = FirebaseStorage.getInstance().getReference("Users").child(userID);

        setRef();
        forRecording();
        generateData();
        generateRecyclerLayout();
        clickListeners();
    }

    private void forRecording() {

        recordButton.setRecordView(recordView);

        recordButton.setListenForRecord(true);

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(profile_page.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);

        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);

        recordView.setSlideToCancelText("Slide To Cancel");

        //recordView.setCustomSounds(1800000, 1800002, 0);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                audioName = "/" + UUID.randomUUID().toString() + ".3gp";
                mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                mFileName += "/recorded_audio.3gp";

//                recordFile = new File(getFilesDir(), audioName);
                recordFile = new File(mFileName);

                try {
//                    audioRecorder.start(recordFile.getPath());
                    audioRecorder.start(mFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
                Toast.makeText(profile_page.this, "Recording Audio Started", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                stopRecording(true);

                Toast.makeText(profile_page.this, "Recording Cancelled", Toast.LENGTH_SHORT).show();

                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording(false);
                uploadAudio();

            }

            @Override
            public void onLessThanSecond() {
                stopRecording(true);

                Toast.makeText(profile_page.this, "Cannot record under 1 second", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });


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

    private void generateRecyclerLayout() {

        rv_myRecordings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_myRecordings.setLayoutManager(linearLayoutManager);

        arrRecordings = new ArrayList<>();
        adapterRecordingsItem = new AdapterRecordingsItem(arrRecordings, getApplicationContext());
        rv_myRecordings.setAdapter(adapterRecordingsItem);
        generateRvData();
        adapterRecordingsItem.notifyDataSetChanged();

    }

    private void generateRvData() {

        Query query = FirebaseDatabase.getInstance().getReference("Audio Recordings")
                        .orderByChild("userID")
                        .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Recordings recordings = dataSnapshot.getValue(Recordings.class);
                        arrRecordings.add(recordings);
                    }

                    adapterRecordingsItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        rv_myRecordings = findViewById(R.id.rv_myRecordings);

        recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);

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

    private void uploadAudio() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy  hh:mm a");
        String date = format.format(Date.parse(dateTime));


        new AlertDialog.Builder(profile_page.this)
                .setTitle("Record Finish")
                .setMessage("Post this recording?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final ProgressDialog progressDialog = new ProgressDialog(profile_page.this);
                        progressDialog.setTitle("Posting Audio Recording...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        String audioPath = recordFile.getPath();
//                        Uri audioUri = Uri.fromFile(new File(audioPath));
                        Uri audioUri = Uri.fromFile(new File(mFileName));

                        Log.d("getPath", recordFile.getPath());
                        Log.d("getAbsolutePath", recordFile.getAbsolutePath());
                        Log.d("toString", recordFile.toString());

                        StorageReference fileReference = userStorage.child("Audio Recordings").child(audioName);

                        fileReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String imageURL = uri.toString();

                                                Recordings recordings = new Recordings(imageURL, audioName, userID, date);

                                                audioDatabase.push().setValue(recordings).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();

//                                                            stopRecording(true);

                                                            Toast.makeText(profile_page.this, "Audio Posted", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(profile_page.this, profile_page.class);
                                                            startActivity(intent);

                                                        } else {
//                                                            stopRecording(true);

                                                            Toast.makeText(profile_page.this, "Posting Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(profile_page.this, profile_page.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile_page.this, "Posting failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(profile_page.this, profile_page.class);
                                        startActivity(intent);
                                    }
                                });



                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        stopRecording(true);
                        Intent intent = new Intent(profile_page.this, profile_page.class);
                        startActivity(intent);
                    }
                })
                .show();
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

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }
}