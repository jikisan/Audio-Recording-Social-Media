package adapter_and_fragmets;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaja.R;

import java.io.IOException;
import java.util.List;

public class SubAdapterTrendingsItem extends RecyclerView.Adapter<SubAdapterTrendingsItem.SubItemViewHolder> {

    private List<Recordings> subArrRecrordings;
    private List<SubItem> subItemList;
    private SubItem subItem;
    int size;
    SubAdapterTrendingsItem.OnItemClickListener onItemClickListener;

    public SubAdapterTrendingsItem() {
    }

    public SubAdapterTrendingsItem(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recordings, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder holder, int position) {


//        Recordings recordings = subArrRecrordings.get(position);
        SubItem subItem = subItemList.get(position);

        String audioName = subItem.audioName;
        String dateTime = subItem.dateTime;
        String audioLink = subItem.audioLink;

        holder.tv_audioName.setText(audioName);
        holder.tv_dateTime.setText(dateTime);

        holder.iv_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try{
                    Toast.makeText(view.getContext(), "Playing", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setDataSource(audioLink);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {

        if(subItemList.size() < 3 )
        {
            return subItemList.size();
        }
        else
        {
            return 3;
        }

    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SubAdapterTrendingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_playButton;
        TextView tv_audioName, tv_dateTime;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_playButton = itemView.findViewById(R.id.iv_playButton);
            tv_audioName = itemView.findViewById(R.id.tv_audioName);
            tv_dateTime = itemView.findViewById(R.id.tv_dateTime);

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
