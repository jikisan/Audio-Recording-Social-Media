package adapter_and_fragmets;

import android.content.Context;
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

public class AdapterRecordingsItem extends RecyclerView.Adapter<AdapterRecordingsItem.ItemViewHolder> {

    List<Recordings> arrRecordings;
    AdapterRecordingsItem.OnItemClickListener onItemClickListener;

    private Context context;

    public AdapterRecordingsItem() {
    }

    public AdapterRecordingsItem(List<Recordings> arrRecordings, Context context) {
        this.arrRecordings = arrRecordings;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recordings, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Recordings recordings = arrRecordings.get(position);

        String audioName = recordings.audioName;
        String dateTime = recordings.dateTime;
        String audioLink = recordings.audioLink;

        holder.tv_audioName.setText(audioName);
        holder.tv_dateTime.setText(dateTime);

        holder.iv_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try{
                    Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setDataSource(audioLink);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//
//
//
//
//                        }
//                    });
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrRecordings.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterRecordingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_playButton;
        TextView tv_audioName, tv_dateTime;

        public ItemViewHolder(@NonNull View itemView) {
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
