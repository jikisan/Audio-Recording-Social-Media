package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import adapter_and_fragmets.AdapterRecordingsItem;
import adapter_and_fragmets.Follow;
import adapter_and_fragmets.Recordings;
import adapter_and_fragmets.Users;

public class user_page extends AppCompatActivity {

    private String userID, userIdFromSearch, sp_fullName;
    private int followerCount = 0;;

    private ImageView iv_userPhoto;
    private EditText et_search;
    private TextView tv_userName, tv_btnFollow, tv_btnUnFollow, tv_followerCount, textView5;
    private FloatingActionButton btn_home;
    private RecyclerView rv_myRecordings;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, followDatabase;

    private ArrayList<Recordings> arrRecordings;
    private ArrayList<Follow> followersCountList = new ArrayList<Follow>();
    private AdapterRecordingsItem adapterRecordingsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        followDatabase = FirebaseDatabase.getInstance().getReference("Follow");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userIdFromSearch = getIntent().getStringExtra("userID");

        setRef();
        generateData(userIdFromSearch);
        generateRecyclerLayout();
        clickListeners();
    }

    private void clickListeners() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_page.this, homepage.class);
                startActivity(intent);
            }
        });

        tv_btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Follow follow = new Follow(userID, userIdFromSearch);

                followDatabase.push().setValue(follow).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(user_page.this, "Followed", Toast.LENGTH_LONG).show();
                        tv_btnFollow.setVisibility(View.INVISIBLE);
                        tv_btnUnFollow.setVisibility(View.VISIBLE);

                        int i = 1;
                        updateFollowersInDB(i);

                        Intent intent = new Intent(user_page.this, user_page.class);
                        intent.putExtra("userID", userIdFromSearch);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(user_page.this, "Failed", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });

        tv_btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(user_page.this)
                        .setTitle("Warning!")
                        .setMessage("Unfollow this user?")
                        .setCancelable(true)
                        .setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Query query = FirebaseDatabase.getInstance().getReference("Follow")
                                        .orderByChild("beingFollowed")
                                        .equalTo(userIdFromSearch);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            Follow follow = dataSnapshot.getValue(Follow.class);

                                            String follower = follow.getFollowers();
                                            if(userID.equals(follower))
                                            {
                                                tv_btnFollow.setVisibility(View.VISIBLE);
                                                tv_btnUnFollow.setVisibility(View.INVISIBLE);
                                                dataSnapshot.getRef().removeValue();

                                                int i = -1;
                                                updateFollowersInDB(i);

                                                Intent intent = new Intent(user_page.this, user_page.class);
                                                intent.putExtra("userID", userIdFromSearch);
                                                startActivity(intent);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                            }
                        })
                        .show();
            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_page.this, search_page.class);
                startActivity(intent);
            }
        });
    }

    private void updateFollowersInDB(int i) {

        int newValue = followerCount + i;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("followers", newValue);

        userDatabase.child(userIdFromSearch).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

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
                .equalTo(userIdFromSearch);

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

    private void generateData(String userIdFromSearch) {

        userDatabase.child(userIdFromSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    sp_fullName =  users.getFullName();
                    String firstName = users.getFirstName();
                    String imageUrl = users.getImageUrl();

                    String capFullname = sp_fullName.substring(0, 1).toUpperCase() + sp_fullName.substring(1);
                    String capFname = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);

                    textView5.setText(capFname + "'s Recordings");

                    tv_userName.setText(capFullname);

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get().load(imageUrl).into(iv_userPhoto);
                    }

                    generateFollowers(userIdFromSearch);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateFollowers(String userIdFromSearch) {

        Query query = FirebaseDatabase.getInstance().getReference("Follow")
                .orderByChild("beingFollowed")
                .equalTo(userIdFromSearch);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Follow follow = dataSnapshot.getValue(Follow.class);

                        String followers = follow.getFollowers();

                        if(userID.equals(followers))
                        {
                            tv_btnFollow.setVisibility(View.INVISIBLE);
                            tv_btnUnFollow.setVisibility(View.VISIBLE);
                        }

                        followersCountList.add(follow);
                        followerCount = followersCountList.size();
                        tv_followerCount.setText(followerCount + "");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {
        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        et_search = findViewById(R.id.et_search);

        tv_userName = findViewById(R.id.tv_userName);
        tv_btnFollow = findViewById(R.id.tv_btnFollow);
        tv_btnUnFollow = findViewById(R.id.tv_btnUnFollow);
        tv_followerCount = findViewById(R.id.tv_followerCount);
        textView5 = findViewById(R.id.textView5);

        btn_home = findViewById(R.id.btn_home);

        rv_myRecordings = findViewById(R.id.rv_myRecordings);
    }
}