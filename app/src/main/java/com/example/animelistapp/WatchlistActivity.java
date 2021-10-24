package com.example.animelistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.animelistapp.adapter.WatchlistAdapter;
import com.example.animelistapp.database.DatabaseClient;
import com.example.animelistapp.database.Task;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WatchlistAdapter watchlistAdapter;

    TextView emptyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setNavigationOnClickListener(V-> finish());

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyTv = findViewById(R.id.emptyTV);


        getWatchList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void getWatchList(){

        class GetWatchlist extends AsyncTask<Void, Void, List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
                List<Task> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                watchlistAdapter = new WatchlistAdapter(WatchlistActivity.this, tasks, emptyTv);
                recyclerView.setAdapter(watchlistAdapter);



                if(tasks.isEmpty()){
                    emptyTv.setVisibility(View.VISIBLE);
                }else{
                    emptyTv.setVisibility(View.GONE);
                }


            }
        }
        GetWatchlist getWatchlist = new GetWatchlist();
        getWatchlist.execute();
    }


}