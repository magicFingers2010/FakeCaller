package com.faker.Faker.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faker.Faker.R;
import com.faker.Faker.model.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder> {
    private java.util.List<audio> List;
    private Context mContext;
    private int new_position;
    private MediaPlayer mp;
    private static final String TAG = "MyActivity";
    Timer timer;
    private Boolean paused = false;
    private Boolean isPlaying = false;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String audioName);
        public void onDialogNegativeClick(String dialog);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton play;
        public ImageButton stop;
        private SeekBar progress;
        private LinearLayout progressLayout;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            play = (ImageButton) view.findViewById(R.id.play);
            stop = (ImageButton) view.findViewById(R.id.stop);
            progress = (SeekBar) view.findViewById(R.id.progress);
            progressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);





        }
    }


    public AudioListAdapter(ArrayList<audio> List, Context mContext, NoticeDialogListener mListener) {
        this.List = List;
        this.mContext =mContext;
        this.mListener = mListener;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_list, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.name.setText(List.get(position).getName().substring(List.get(position).getName().lastIndexOf("/")+ 1));
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mListener.onDialogPositiveClick(List.get(position).getName());
            }
        });

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    isPlaying = true;
                    holder.stop.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                    holder.progressLayout.setVisibility(View.VISIBLE);

                        Uri audio = Uri.parse(List.get(position).getName());
                        try {
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setDataSource(mContext, audio);
                            mp.prepare();
                        } catch (IOException e) {
                            Log.d(TAG, "mp.error()");
                        }
                        mp.start();

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            holder.progressLayout.setVisibility(View.GONE);
                            holder.stop.setVisibility(View.GONE);
                            holder.play.setVisibility(View.VISIBLE);
                            isPlaying = false;
                            mp.reset();
                        }
                    });
                    holder.progress.setMax(mp.getDuration());
                    timer = new Timer();
                    try {
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                holder.progress.setProgress(mp.getCurrentPosition());
                            }
                        }, 0, 1000);
                    }catch (Exception e){
                        Log.d(TAG,e.getMessage());
                    }

                }
            }

        });
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                timer.purge();
                List.get(position).setPaused(true);
                paused = true;
                isPlaying = false;
                mp.release();
                v.setVisibility(View.GONE);
               holder.play.setVisibility(View.VISIBLE);
               holder.progressLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return List.size();
    }

}
