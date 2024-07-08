package com.bilcodes.animestack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import com.bilcodes.animestack.adapter.WatchlistAdapter;
import com.bilcodes.animestack.database.DatabaseClient;
import com.bilcodes.animestack.database.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        finish();
        return super.getOnBackInvokedDispatcher();
    }

    void getWatchList(){



        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            //background work
            List<Task> taskList = DatabaseClient
                    .getInstance(getApplicationContext())
                    .getAppDatabase()
                    .taskDao()
                    .getAll();
            handler.post(() -> {
                //ui thread work
                watchlistAdapter = new WatchlistAdapter(WatchlistActivity.this, taskList, emptyTv);
                recyclerView.setAdapter(watchlistAdapter);



                if(taskList.isEmpty()){
                    emptyTv.setVisibility(View.VISIBLE);
                }else{
                    emptyTv.setVisibility(View.GONE);
                }
            });

        });
    }


}