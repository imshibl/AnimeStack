package com.bilcodes.animestack.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bilcodes.animestack.R;
import com.bilcodes.animestack.database.DatabaseClient;
import com.bilcodes.animestack.database.Task;
import com.bilcodes.animestack.model.AnimeModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.MyHolder> {
    Context context;
    List<AnimeModel> animeModelList;

    public AnimeRecyclerAdapter(Context context, List<AnimeModel> animeModelList) {
        this.context = context;
        this.animeModelList = animeModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.anime_list_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        try {
            String name = animeModelList.get(position).getTitle();
            String rating = animeModelList.get(position).getRating();
            String cover = animeModelList.get(position).getCoverImage();
            holder.title.setText(name);
            holder.rating.setText(rating);

            String desc = animeModelList.get(position).getDescription();
//            String halfDesc = desc.substring(0, 80);
            holder.description.setText(desc);

            holder.type.setText(String.format("Type:%s", animeModelList.get(position).getSubType()));
            holder.ageRating.setText(String.format("Age Rating:%s", animeModelList.get(position).getAgeRating()));

            String subType = animeModelList.get(position).getSubType();
            if (subType.equals("movie")) {
                holder.status.setVisibility(View.INVISIBLE);
            } else {
                holder.status.setText(String.format("Status:%s", animeModelList.get(position).getStatus()));
            }


            Glide.with(context)
                    .load(animeModelList.get(position).getPosterImage())
                    .placeholder(R.drawable.loading1)
                    .into(holder.posterImage);

            holder.animeCard.setOnClickListener(V -> {
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);
                View view = View.inflate(context, R.layout.custom_alert_dialog, null);

                TextView title = view.findViewById(R.id.title);
                ImageView poster = view.findViewById(R.id.poster);
                ImageView coverImg = view.findViewById(R.id.Cover_img);
                TextView rating1 = view.findViewById(R.id.rating);
                TextView type = view.findViewById(R.id.type);
                TextView ageRating = view.findViewById(R.id.ageRating);
                TextView description = view.findViewById(R.id.description);


                title.setText(name);
                rating1.setText(rating);
                type.setText(String.format("Type:%s", animeModelList.get(position).getSubType()));
                ageRating.setText(String.format("Age Rating:%s", animeModelList.get(position).getAgeRating()));
                description.setText(desc);
                Glide.with(context)
                        .load(animeModelList.get(position).getPosterImage())
                        .placeholder(R.drawable.loading1)
                        .into(poster);
                try {
                    if (!(cover == null)) {
                        Glide.with(context)
                                .load(cover)
                                .placeholder(R.drawable.loading1)
                                .into(coverImg);
                    } else {
                        poster.setVisibility(View.GONE);
                        coverImg.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    poster.setVisibility(View.GONE);
                    coverImg.setVisibility(View.GONE);
                }


                dialogBox.setView(view);
                dialogBox.create();
                dialogBox.show();
            });

            holder.animeCard.setOnLongClickListener(V -> {
                saveTask(position);
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveTask(int position) {
        String title = animeModelList.get(position).getTitle();
        String type = animeModelList.get(position).getSubType();
        String description = animeModelList.get(position).getDescription();



        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            Task task = new Task();
            task.setTitle(title);
            task.setType(type);
            task.setDescription(description);

            boolean dataExits;


            dataExits = DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                    .taskDao().exists(title);

            if (dataExits) {
                Log.d("appdata", "already here");
            } else {
                DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(task);
            }

            handler.post(() -> {
                if (dataExits) {
                    Toast.makeText(context, "Already available in watchlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Added to watchlist", Toast.LENGTH_SHORT).show();
                }

            });

        });
    }

    @Override
    public int getItemCount() {
        return animeModelList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView posterImage;
        TextView title, rating, description, type, ageRating, status;
        MaterialCardView animeCard;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            posterImage = itemView.findViewById(R.id.poster_image);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating);
            description = itemView.findViewById(R.id.description);
            type = itemView.findViewById(R.id.subType);
            ageRating = itemView.findViewById(R.id.ageRating);
            status = itemView.findViewById(R.id.status);

            animeCard = itemView.findViewById(R.id.anime_card);
        }
    }

}
