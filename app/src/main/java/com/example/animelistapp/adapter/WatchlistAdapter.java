package com.example.animelistapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animelistapp.R;
import com.example.animelistapp.database.Task;
import com.example.animelistapp.model.AnimeModel;

import org.w3c.dom.Text;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.MyHolder> {

    Context mContext;
    List<Task> watchList;

    public WatchlistAdapter(Context mContext, List<Task> watchList) {
        this.mContext = mContext;
        this.watchList = watchList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.watch_list_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Task t = watchList.get(position);

        holder.title.setText(t.getTitle());
        holder.type.setText(t.getType());


    }

    @Override
    public int getItemCount() {
        return watchList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView type;
        ImageView deleteIcon;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_name);
            type = itemView.findViewById(R.id.type);
            deleteIcon = itemView.findViewById(R.id.delete);
        }
    }
}
