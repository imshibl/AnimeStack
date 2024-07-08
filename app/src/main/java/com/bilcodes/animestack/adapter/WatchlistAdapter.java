package com.bilcodes.animestack.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bilcodes.animestack.R;
import com.bilcodes.animestack.database.DatabaseClient;
import com.bilcodes.animestack.database.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.MyHolder> {

    Context mContext;
    List<Task> watchList;

    TextView emptyTV;


    public WatchlistAdapter(Context mContext, List<Task> watchList, TextView emptyTV) {
        this.mContext = mContext;
        this.watchList = watchList;
        this.emptyTV = emptyTV;

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

        holder.deleteIcon.setOnClickListener(V -> {
            watchList.remove(t);
            deleteItem(t);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, watchList.size());
//            notifyDataSetChanged();


            if(watchList.isEmpty()){
                emptyTV.setVisibility(View.VISIBLE);
            }


        });


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

    private void deleteItem(final Task task) {


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            DatabaseClient.getInstance(mContext)
                    .getAppDatabase()
                    .taskDao()
                    .delete(task);
            handler.post(() -> Toast.makeText(mContext, "Removed", Toast.LENGTH_SHORT).show());

        });

    }
}
