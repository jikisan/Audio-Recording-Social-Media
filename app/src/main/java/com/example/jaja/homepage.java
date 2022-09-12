package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import adapter_and_fragmets.AdapterRecordingsItem;
import adapter_and_fragmets.AdapterTrendingsItem;
import adapter_and_fragmets.Recordings;
import adapter_and_fragmets.Users;

public class homepage extends AppCompatActivity {

    private ImageView iv_bannerPhoto;
    private EditText et_search;
    private RecyclerView rv_trendings;

    private Uri imageUri;
    private String userID, tempImageName;
    private ArrayList<Users> arrUsers;
    private AdapterTrendingsItem adapterTrendingsItem;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        
        setRef();
        generateData();
        generateRecyclerLayout();
        clickListeners();
    }

    private void generateData() {

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_imageUrl = userProfile.getImageUrl();
                    tempImageName = userProfile.getImageName();

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_bannerPhoto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(homepage.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generateRecyclerLayout() {

        rv_trendings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_trendings.setLayoutManager(linearLayoutManager);

        arrUsers = new ArrayList<>();
        adapterTrendingsItem = new AdapterTrendingsItem(arrUsers, getApplicationContext());
        rv_trendings.setAdapter(adapterTrendingsItem);
        generateRvData();
        adapterTrendingsItem.notifyDataSetChanged();
    }

    private void generateRvData() {

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Users users = dataSnapshot.getValue(Users.class);
                        arrUsers.add(users);
                    }

                    adapterTrendingsItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clickListeners() {
        iv_bannerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homepage.this, profile_page.class);
                startActivity(intent);
            }
        });
    }

    private void setRef() {
       et_search = findViewById(R.id.et_search);
       iv_bannerPhoto = findViewById(R.id.iv_bannerPhoto);
        rv_trendings = findViewById(R.id.rv_trendings);
    }
}