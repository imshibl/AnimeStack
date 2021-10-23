package com.example.animelistapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animelistapp.R;
import com.example.animelistapp.WatchlistActivity;
import com.example.animelistapp.database.DatabaseClient;
import com.example.animelistapp.database.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        holder.deleteIcon.setOnClickListener(V -> {
            watchList.remove(t);
            deleteItem(t);
            notifyItemRemoved(position);


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
        class Delete extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(mContext)
                        .getAppDatabase()
                        .taskDao()
                        .delete(task);

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Toast.makeText(mContext, "Removed", Toast.LENGTH_SHORT).show();

            }
        }
        Delete dt = new Delete();
        dt.execute();

    }
}
