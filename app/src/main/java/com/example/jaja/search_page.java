package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import adapter_and_fragmets.AdapterUsersItem;
import adapter_and_fragmets.Users;

public class search_page extends AppCompatActivity {

    private SearchView sv_search;
    private FloatingActionButton btn_home;

    private ArrayList<Users> arrUsers, arr;
    private RecyclerView recyclerView_searches;
    private AdapterUsersItem adapterUsersItem;

    private String userID, myFullName;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, audioDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        audioDatabase = FirebaseDatabase.getInstance().getReference("Audio Recordings");
        userStorage = FirebaseStorage.getInstance().getReference("Users").child(userID);

        setRef();
        generateUsers();
        generateRecyclerLayout();

        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_page.this, homepage.class);
                startActivity(intent);
            }
        });
    }

    private void generateUsers() {

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                   for(DataSnapshot dataSnapshot : snapshot.getChildren())
                   {
                       Users users = dataSnapshot.getValue(Users.class);

                       if(users.getUserId().equals(userID))
                       {
                           continue;
                       }

                       arrUsers.add(users);
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generateRecyclerLayout() {

        recyclerView_searches.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_searches.setLayoutManager(linearLayoutManager);

        arrUsers = new ArrayList<>();
    }

    private void search(String s) {
        arr = new ArrayList<>();
        for(Users object : arrUsers)
        {
            if (object.getFullName().toLowerCase().contains(s.toLowerCase()))
            {
                arr.add(object);
            }

            if(s.isEmpty())
            {
                arr.clear();
            }

            adapterUsersItem = new AdapterUsersItem(arr);
            recyclerView_searches.setAdapter(adapterUsersItem);
        }

        adapterUsersItem.setOnItemClickListener(new AdapterUsersItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String userID = arr.get(position).getUserId();
                Intent intent = new Intent(search_page.this, user_page.class);
                intent.putExtra("userID", userID);
                startActivity(intent);

            }
        });

    }

    private void setRef() {

        sv_search = findViewById(R.id.sv_search);
        recyclerView_searches = findViewById(R.id.recyclerView_searches);
        btn_home = findViewById(R.id.btn_home);
    }
}