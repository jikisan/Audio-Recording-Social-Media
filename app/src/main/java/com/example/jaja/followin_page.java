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
import adapter_and_fragmets.AdapterRecordingsItem;
import adapter_and_fragmets.Follow;
import adapter_and_fragmets.Recordings;

public class followin_page extends AppCompatActivity {

    private String userID;

    private RecyclerView rv_followings;
    private LinearLayout backBtn;

    private AdapterFollowingsItem adapterRecordingsItem;
    private ArrayList<Follow> arrFollowing = new ArrayList<Follow>();

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, followDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followin_page);

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
                Intent intent = new Intent(followin_page.this, profile_page.class);
                startActivity(intent);
            }
        });

        adapterRecordingsItem.setOnItemClickListener(new AdapterFollowingsItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = arrFollowing.get(position).getBeingFollowed();

                Intent intent = new Intent(followin_page.this, user_page.class);
                intent.putExtra("userID", id);
                startActivity(intent);
            }
        });
    }

    private void generateRecyclerLayout() {

        rv_followings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_followings.setLayoutManager(linearLayoutManager);

        arrFollowing = new ArrayList<>();
        adapterRecordingsItem = new AdapterFollowingsItem(arrFollowing);
        rv_followings.setAdapter(adapterRecordingsItem);

        generateRvData();
        adapterRecordingsItem.notifyDataSetChanged();
    }

    private void generateRvData() {

        Query query = FirebaseDatabase.getInstance().getReference("Follow")
                .orderByChild("followers")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Follow follow = dataSnapshot.getValue(Follow.class);
                        arrFollowing.add(follow);
                    }

                    adapterRecordingsItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        rv_followings = findViewById(R.id.rv_followings);
        backBtn = findViewById(R.id.backBtn);
    }
}