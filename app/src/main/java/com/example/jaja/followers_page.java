package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import adapter_and_fragmets.AdapterFollowingsItem;
import adapter_and_fragmets.AdapterUsersItem;
import adapter_and_fragmets.Follow;
import adapter_and_fragmets.Users;

public class followers_page extends AppCompatActivity {

    private String userID;

    private RecyclerView rv_followers;
    private LinearLayout backBtn;

    private AdapterUsersItem adapterUsersItem;
    private ArrayList<Users> arrFolowers = new ArrayList<Users>();

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, followDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        followDatabase = FirebaseDatabase.getInstance().getReference("Follow");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef();
        generateRecyclerLayout();
        clickListeners();
    }

    private void clickListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(followers_page.this, profile_page.class);
                startActivity(intent);
            }
        });

        adapterUsersItem.setOnItemClickListener(new AdapterUsersItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = arrFolowers.get(position).getUserId();

                Intent intent = new Intent(followers_page.this, user_page.class);
                intent.putExtra("userID", id);
                startActivity(intent);

            }
        });
    }

    private void generateRecyclerLayout() {

        rv_followers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_followers.setLayoutManager(linearLayoutManager);

        arrFolowers = new ArrayList<>();
        adapterUsersItem = new AdapterUsersItem(arrFolowers);
        rv_followers.setAdapter(adapterUsersItem);

        generateRvData();
        adapterUsersItem.notifyDataSetChanged();
    }

    private void generateRvData() {

        Query query = FirebaseDatabase.getInstance().getReference("Follow")
                .orderByChild("beingFollowed")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Follow follow = dataSnapshot.getValue(Follow.class);
                        String followersId = follow.getFollowers();

                        generateFollowersData(followersId);

                    }

                    adapterUsersItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateFollowersData(String followersId) {

        userDatabase.child(followersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    arrFolowers.add(users);
                    adapterUsersItem.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        rv_followers = findViewById(R.id.rv_followers);
        backBtn = findViewById(R.id.backBtn);
    }
}