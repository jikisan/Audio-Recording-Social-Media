package adapter_and_fragmets;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaja.R;
import com.example.jaja.followin_page;
import com.example.jaja.user_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterFollowingsItem extends RecyclerView.Adapter<AdapterFollowingsItem.ItemViewHolder> {

    List<Follow> arrFollow;
    OnItemClickListener onItemClickListener;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    int followerCount = 0;

    private DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");

    public AdapterFollowingsItem() {
    }

    public AdapterFollowingsItem(List<Follow> arrFollow) {
        this.arrFollow = arrFollow;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_followings, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        Follow follow = arrFollow.get(position);

        String followingId = follow.beingFollowed;

        getFollowingUsers(followingId, holder);

        holder.tv_btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = FirebaseDatabase.getInstance().getReference("Follow")
                        .orderByChild("beingFollowed")
                        .equalTo(followingId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Follow follow = dataSnapshot.getValue(Follow.class);

                            String follower = follow.getFollowers();
                            if(userID.equals(follower))
                            {
                                dataSnapshot.getRef().removeValue();

                                int i = -1;
                                updateFollowersInDB(i, followingId);

                                Intent intent = new Intent(view.getContext(), followin_page.class);
                                view.getContext().startActivity(intent);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void updateFollowersInDB(int i, String followingId) {

        int newValue = followerCount + i;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("followers", newValue);

        userDatabase.child(followingId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    private void getFollowingUsers(String followingId, ItemViewHolder holder) {

        userDatabase.child(followingId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    String sp_fullName = users.getFirstName();
                    followerCount = users.getFollowers();
                    String imageUrl = users.getImageUrl();

                    holder.tv_userFullName.setText(sp_fullName);
                    holder.tv_userFollowers.setText(followerCount + " Followers");

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get().load(imageUrl).into(holder.iv_userProfile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrFollow.size();
    }


    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterFollowingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userProfile;
        TextView tv_userFullName, tv_userFollowers, tv_btnUnFollow;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userProfile = itemView.findViewById(R.id.iv_userProfile);
            tv_userFullName = itemView.findViewById(R.id.tv_userFullName);
            tv_userFollowers = itemView.findViewById(R.id.tv_userFollowers);
            tv_btnUnFollow = itemView.findViewById(R.id.tv_btnUnFollow);

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
