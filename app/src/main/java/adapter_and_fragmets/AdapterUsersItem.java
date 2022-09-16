package adapter_and_fragmets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaja.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsersItem extends RecyclerView.Adapter<AdapterUsersItem.ItemViewHolder> {

    List<Users> arrUsers;
    OnItemClickListener onItemClickListener;

    public AdapterUsersItem() {
    }

    public AdapterUsersItem(List<Users> arrUsers) {
        this.arrUsers = arrUsers;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Users users = arrUsers.get(position);

        String fullName = users.fullName;
        String imageUrl = users.imageUrl;
        int followers = users.followers;

        holder.tv_userFullName.setText(fullName);
        holder.tv_userFollowers.setText(followers + " followers");

        if(!imageUrl.isEmpty())
        {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.iv_userProfile);
        }

    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterUsersItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userProfile;
        TextView tv_userFullName, tv_userFollowers;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userProfile = itemView.findViewById(R.id.iv_userProfile);
            tv_userFullName = itemView.findViewById(R.id.tv_userFullName);
            tv_userFollowers = itemView.findViewById(R.id.tv_userFollowers);

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
