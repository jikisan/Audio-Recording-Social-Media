package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import adapter_and_fragmets.AdapterRecordingsItem;
import adapter_and_fragmets.AdapterTrendingsItem;
import adapter_and_fragmets.Item;
import adapter_and_fragmets.ListOfSubItem;
import adapter_and_fragmets.Recordings;
import adapter_and_fragmets.SubItem;
import adapter_and_fragmets.Users;

public class homepage extends AppCompatActivity {

    private ImageView iv_bannerPhoto;
    private TextView et_search;
    private RecyclerView rv_trendings;

    private Uri imageUri;
    private String userID, tempImageName, userCount;
//    private String[]  imageUrl;
//    private String[]  fullName;
//    private String[]  id;
    private int recordingCount = 0;


    private ArrayList<Users> arrUsers;
    private ArrayList<Recordings> arrRecordings;

    private List<Item> itemList = new ArrayList<>();
    private List<SubItem> subItemList = new ArrayList<>();
    private List<Integer> list = new ArrayList<Integer>();
    private List<List<SubItem>> listOfSubItemList = new ArrayList<List<SubItem>>();

    private List<String> imageUrlList = new ArrayList<>();
    private List<String> fullNameList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();
    private List<Integer> followersList = new ArrayList<>();

    private AdapterTrendingsItem adapterTrendingsItem;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, audioDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        audioDatabase = FirebaseDatabase.getInstance().getReference("Audio Recordings");
        
        setRef();
//        generateUserCount();
        generateData();
        generateRecyclerLayout();
        clickListeners();
    }

    private void clickListeners() {
        iv_bannerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homepage.this, profile_page.class);
                startActivity(intent);
            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homepage.this, search_page.class);
                startActivity(intent);
            }
        });

//        adapterTrendingsItem.setOnItemClickListener(new AdapterTrendingsItem.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//                String userID = arrUsers.get(position).getUserId();
//                Intent intent = new Intent(homepage.this, user_page.class);
//                intent.putExtra("userID", userID);
//                startActivity(intent);
//
//            }
//        });
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
        generateRvData();

        rv_trendings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_trendings.setLayoutManager(linearLayoutManager);

        arrUsers = new ArrayList<>();
        arrRecordings = new ArrayList<>();


        adapterTrendingsItem = new AdapterTrendingsItem( itemList, getApplicationContext());
        rv_trendings.setAdapter(adapterTrendingsItem);


    }

    private void generateRvData() {

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                        Users users = dataSnapshot.getValue(Users.class);

                        if(userID.equals(users.getUserId()))
                        {
                            continue;
                        }

                        arrUsers.add(users);

                        String imageUrl = users.getImageUrl();
                        String fullName = users.getFullName();
                        String id = users.getUserId();
                        int followers = users.getFollowers();

                        imageUrlList.add(imageUrl);
                        fullNameList.add(fullName);
                        idList.add(id);
                        followersList.add(followers);

                        int i = recordingCount;
                        Item item = new Item(imageUrl, fullName, id, followers, subItemList);

                        itemList.add(item);
//                        generateSubItem(imageUrl, fullName, id, followers);
                    }

                    Collections.sort(itemList, ratingsLowestToHighest);
                    adapterTrendingsItem.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateSubItem(String imageUrl, String fullName, String id, int followers) {

        Query query = FirebaseDatabase.getInstance().getReference("Audio Recordings")
                .orderByChild("userID")
                .equalTo(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String audioName;
                String dateTime;
                String audioLink;

                if(snapshot.exists()){

                    subItemList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Recordings recordings = dataSnapshot.getValue(Recordings.class);
                        arrRecordings.add(recordings);

                        audioName = recordings.getAudioName();
                        dateTime = recordings.getDateTime();
                        audioLink = recordings.getAudioLink();

                        SubItem subItem = new SubItem(audioName, dateTime, audioLink, recordingCount);
                        subItemList.add(subItem);

                    }


                    Collections.reverse(subItemList);
                    listOfSubItemList.add(subItemList);

                    int i = recordingCount;
                    Item item = new Item(imageUrl, fullName, id, followers, listOfSubItemList.get(i));
                    Collections.sort(itemList, ratingsLowestToHighest);
                    itemList.add(item);

                    Log.d("Count: ", String.valueOf(recordingCount) );
                    Log.d("SubItemList: ", listOfSubItemList.get(recordingCount).toString() );
                    Log.d("SubItemList Size: ", String.valueOf(listOfSubItemList.get(recordingCount).size()) );
                    Log.d("listOfSubItemList Size: ", String.valueOf(listOfSubItemList.size()) );
                    recordingCount += 1;

                    adapterTrendingsItem.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public Comparator<Item> ratingsLowestToHighest = new Comparator<Item>() {
        @Override
        public int compare(Item item, Item t1) {
            return String.valueOf(item.getFollowers()).compareToIgnoreCase(String.valueOf(t1.getFollowers()));
        }
    };

    private void setRef() {
       et_search = findViewById(R.id.et_search);
       iv_bannerPhoto = findViewById(R.id.iv_bannerPhoto);
       rv_trendings = findViewById(R.id.rv_trendings);
    }
}