package adapter_and_fragmets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaja.R;
import com.example.jaja.homepage;
import com.example.jaja.user_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterTrendingsItem extends RecyclerView.Adapter<AdapterTrendingsItem.ItemViewHolder> {

    List<Users> arrUsers;
    List<Recordings> recordingsList;
    List<Item> itemList;
    List<SubItem> subItemList = new ArrayList<>();
    List<List<SubItem>> listOfSubItemList = new ArrayList<List<SubItem>>();

    AdapterTrendingsItem.OnItemClickListener onItemClickListener;

    private Context context;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private String userID;
    private FirebaseUser user;

    public AdapterTrendingsItem() {
    }

    public AdapterTrendingsItem(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trendings, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Item item = itemList.get(position);

        recordingsList = new ArrayList<>();

        String imageUrl = item.imageUrl;
        String fullName = item.fullName;
        int followers = item.followers;
        String id = item.userId;

        holder.tv_trendingUserName.setText(fullName);
        holder.tv_userFollowers.setText(followers + " followers");

        if(!imageUrl.isEmpty())
        {
            Picasso.get().load(imageUrl).into(holder.iv_trendingUserPhoto);
        }

        holder.rv_trendingsRecordings.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rv_trendingsRecordings.getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        holder.rv_trendingsRecordings.setLayoutManager(layoutManager);

        SubAdapterTrendingsItem subAdapterTrendingsItem = new SubAdapterTrendingsItem(item.subItemList);
        holder.rv_trendingsRecordings.setAdapter(subAdapterTrendingsItem);

        generateSublist(id, subAdapterTrendingsItem);
        subAdapterTrendingsItem.notifyDataSetChanged();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, user_page.class);
                intent.putExtra("userID", id);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void generateSublist(String id, SubAdapterTrendingsItem subAdapterTrendingsItem) {

        Query query = FirebaseDatabase.getInstance().getReference("Audio Recordings")
                .orderByChild("userID")
                .equalTo(id);

       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               if(snapshot.exists()){
                   subItemList.clear();

                   for(DataSnapshot dataSnapshot : snapshot.getChildren())
                   {
                       Recordings recordings = dataSnapshot.getValue(Recordings.class);

                       int recordingCount = 0;
                       String audioName = recordings.getAudioName();
                       String dateTime = recordings.getDateTime();
                       String audioLink = recordings.getAudioLink();

                       SubItem subItem = new SubItem(audioName, dateTime, audioLink, recordingCount);
                       subItemList.add(subItem);

                   }

                   Collections.reverse(subItemList);
                   subAdapterTrendingsItem.notifyDataSetChanged();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterTrendingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_trendingUserName, tv_userFollowers;
        ImageView iv_trendingUserPhoto;
        RecyclerView rv_trendingsRecordings;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_trendingUserName = itemView.findViewById(R.id.tv_trendingUserName);
            tv_userFollowers = itemView.findViewById(R.id.tv_userFollowers);
            iv_trendingUserPhoto = itemView.findViewById(R.id.iv_trendingUserPhoto);
            rv_trendingsRecordings = itemView.findViewById(R.id.rv_trendingsRecordings);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
