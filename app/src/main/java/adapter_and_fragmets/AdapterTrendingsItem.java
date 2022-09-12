package adapter_and_fragmets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaja.R;
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
    AdapterTrendingsItem.OnItemClickListener onItemClickListener;
    private Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    private String userID;
    private FirebaseUser user;

    public AdapterTrendingsItem() {
    }

    public AdapterTrendingsItem(List<Users> arrUsers, Context context) {
        this.arrUsers = arrUsers;
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Users users = arrUsers.get(position);

        String imageUrl = users.imageUrl;
        String fullName = users.fullName;

        holder.tv_trendingUserName.setText(fullName);

        Picasso.get().load(imageUrl).into(holder.iv_trendingUserPhoto);

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rv_trendingsRecordings.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        recordingsList = new ArrayList<>();

        layoutManager.setStackFromEnd(false);
        layoutManager.setInitialPrefetchItemCount(recordingsList.size());

        // Create sub item view adapter

        SubAdapterTrendingsItem subAdapterTrendingsItem = new SubAdapterTrendingsItem(recordingsList);

        holder.rv_trendingsRecordings.setLayoutManager(layoutManager);
        holder.rv_trendingsRecordings.setAdapter(subAdapterTrendingsItem);
        holder.rv_trendingsRecordings.setRecycledViewPool(viewPool);
        generateSubRvData(users, subAdapterTrendingsItem);
        subAdapterTrendingsItem.notifyDataSetChanged();

    }

    private void generateSubRvData(Users users, SubAdapterTrendingsItem subAdapterTrendingsItem) {
        Query query = FirebaseDatabase.getInstance().getReference("Audio Recordings")
                .orderByChild("userID")
                .equalTo(users.userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Recordings recordings = dataSnapshot.getValue(Recordings.class);
                        recordingsList.add(recordings);
                    }

                    Collections.reverse(recordingsList);
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
        return arrUsers.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterTrendingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_trendingUserName;
        ImageView iv_trendingUserPhoto;
        RecyclerView rv_trendingsRecordings;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_trendingUserName = itemView.findViewById(R.id.tv_trendingUserName);
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
