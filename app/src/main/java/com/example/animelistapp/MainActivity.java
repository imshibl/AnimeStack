package com.example.animelistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animelistapp.adapter.AnimeRecyclerAdapter;
import com.example.animelistapp.database.DatabaseClient;
import com.example.animelistapp.database.Task;
import com.example.animelistapp.model.AnimeModel;
import com.example.animelistapp.networking.Networking;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    List<AnimeModel> animeModelList;
    AnimeRecyclerAdapter animeRecyclerAdapter;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;

    LinearLayout sortingOptionsArea;

    FloatingActionButton floatingActionButton;

    ImageView watchListButton;

    Networking networking;

    MaterialCardView allButton, topRatedButton, popularButton, favoritesButton, moviesButton, mostWatchedButton;

    SharedPreferences sharedPreferences;

    int page = 0;
    //total pages
    int pageLimit = 17000;

    String sort = "all";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

        int firstOpening = sharedPreferences.getInt("first_opening", 0);

        animeModelList = new ArrayList<>();
        progressBar = findViewById(R.id.circular_progressBar);
        nestedScrollView = findViewById(R.id.nested_scrollView);
        recyclerView = findViewById(R.id.recycler_view);
        sortingOptionsArea = findViewById(R.id.sorting_options);
        floatingActionButton = findViewById(R.id.fab_button);
        watchListButton = findViewById(R.id.watchlist_toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        floatingActionButton.setVisibility(View.GONE);

        networking = new Networking(this, progressBar, animeRecyclerAdapter, sortingOptionsArea, recyclerView, animeModelList);

        allButton = findViewById(R.id.all_button);
        topRatedButton = findViewById(R.id.top_rated_button);
        popularButton = findViewById(R.id.popular_button);
        favoritesButton = findViewById(R.id.favorites_button);
        moviesButton = findViewById(R.id.movies_button);
        mostWatchedButton = findViewById(R.id.most_watched_button);

        //custom tool/appbar bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(firstOpening == 0){
            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.watchlist_toolbar), "Watch Later", "Long Press On Desired Anime To Add Into Your Watch Later List")
                    .cancelable(true)
                    .transparentTarget(true));
            sharedPrefEditor.putInt("first_opening", 1);
            sharedPrefEditor.apply();
//            sharedPrefEditor.commit();
        }



        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Anime Stack");
        //drawer and nav view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_nav);
        navigationView.setItemIconTintList(null);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        watchListButton.setOnClickListener(V->{
            startActivity(new Intent(this, WatchlistActivity.class));

        });

        //drawer navigation view items controls
        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.watchlist) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, WatchlistActivity.class));
            } else if (item.getItemId() == R.id.share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String url = "http://play.google.com/store/apps/details?id=" + this.getPackageName();
                String shareBody = "Anime Stack : Collection of more than 45k+ anime shows and movie details\nInstall Now:\n" + url;
                String shareSub = "Check This App";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            } else if (item.getItemId() == R.id.rate) {
                askRating();
            }
            return true;

        });


//always when starting app
        if (animeModelList.isEmpty()) {
            sortingOptionsArea.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        networking.getAllData(page, pageLimit, sort);
        changeButton(allButton, topRatedButton, popularButton, favoritesButton, moviesButton, mostWatchedButton);



//        Handler handler1 = new Handler();
//        handler1.postDelayed(() -> {
//            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(1);
//            View view = viewHolder.itemView;
//            TapTargetView.showFor(this, TapTarget.forView(view, "Tap", "Tap on item to display full details and long press on item to add in to watch list")
//            .cancelable(true)
//            .drawShadow(true)
//            .tintTarget(true));
//
//        }, 4000);


//sorting
        allButton.setOnClickListener(v -> {
            sort = "all";
            animeModelList.clear();
            networking.updateData();


            page = 0;

            changeButton(allButton, topRatedButton, popularButton, favoritesButton, moviesButton, mostWatchedButton);
            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);
        });

        topRatedButton.setOnClickListener(v -> {
            sort = "top_rated";
            animeModelList.clear();
            networking.updateData();
            page = 0;

            changeButton(topRatedButton, allButton, popularButton, favoritesButton, moviesButton, mostWatchedButton);


            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);
        });
        popularButton.setOnClickListener(v -> {
            sort = "popular";
            animeModelList.clear();
            networking.updateData();
            page = 0;

            changeButton(popularButton, topRatedButton, allButton, favoritesButton, moviesButton, mostWatchedButton);
            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);

        });
        favoritesButton.setOnClickListener(v -> {
            sort = "favorites";
            animeModelList.clear();
            networking.updateData();
            page = 0;

            changeButton(favoritesButton, topRatedButton, popularButton, allButton, moviesButton, mostWatchedButton);
            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);
        });


        moviesButton.setOnClickListener(v -> {
            sort = "movies";
            animeModelList.clear();
            networking.updateData();
            page = 0;

            changeButton(moviesButton, topRatedButton, popularButton, favoritesButton, allButton, mostWatchedButton);
            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);
        });
        mostWatchedButton.setOnClickListener(v -> {
            sort = "most_watched";
            animeModelList.clear();
            networking.updateData();
            page = 0;

            changeButton(mostWatchedButton, topRatedButton, popularButton, favoritesButton, moviesButton, allButton);
            Handler handler = new Handler();
            handler.postDelayed(() -> networking.getAllData(page, pageLimit, sort), 2000);

        });


        //pagination
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                page = page + 10;
                progressBar.setVisibility(View.VISIBLE);
                networking.getAllData(page, pageLimit, sort);
            }
        });

        //go to top of screen
        floatingActionButton.setOnClickListener(v -> {
            nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
            getWatchList();
        });

        //show/hide floating action button
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = nestedScrollView.getScrollY();
            if (scrollY > 50) {
                floatingActionButton.setVisibility(View.VISIBLE);
            } else {
                floatingActionButton.setVisibility(View.GONE);
            }
        });


    }

    private void getWatchList() {
        class GetTasks extends AsyncTask<Void, Void, List<Task>>{

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
                for(int i = 0; i<tasks.size(); i++){
                    Log.d("appdata:", tasks.get(i).getTitle());
                    Log.d("appdata:", tasks.get(i).getType());
//                    Log.d("appdata:", tasks.get(i).getDescription());
                }
            }
        }
        GetTasks gt = new GetTasks();
        gt.execute();
    }


    private void askRating() {

        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent openMarket = new Intent(Intent.ACTION_VIEW, uri);

        openMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            startActivity(openMarket);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void changeButton(MaterialCardView selectedButton, MaterialCardView otherBtn1, MaterialCardView otherBtn2, MaterialCardView otherBtn3, MaterialCardView otherBtn4, MaterialCardView otherBtn5) {
        selectedButton.setStrokeColor(getResources().getColor(R.color.white));
        selectedButton.setCardBackgroundColor(getResources().getColor(R.color.white));
        selectedButton.setEnabled(false);

        otherBtn1.setStrokeColor(getResources().getColor(R.color.cardStrokeColor));
        otherBtn1.setCardBackgroundColor(getResources().getColor(R.color.sortBtnBG));
        otherBtn1.setEnabled(true);

        otherBtn2.setStrokeColor(getResources().getColor(R.color.cardStrokeColor));
        otherBtn2.setCardBackgroundColor(getResources().getColor(R.color.sortBtnBG));
        otherBtn2.setEnabled(true);

        otherBtn3.setStrokeColor(getResources().getColor(R.color.cardStrokeColor));
        otherBtn3.setCardBackgroundColor(getResources().getColor(R.color.sortBtnBG));
        otherBtn3.setEnabled(true);


        otherBtn4.setStrokeColor(getResources().getColor(R.color.cardStrokeColor));
        otherBtn4.setCardBackgroundColor(getResources().getColor(R.color.sortBtnBG));
        otherBtn4.setEnabled(true);

        otherBtn5.setStrokeColor(getResources().getColor(R.color.cardStrokeColor));
        otherBtn5.setCardBackgroundColor(getResources().getColor(R.color.sortBtnBG));
        otherBtn5.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem item = menu.findItem(R.id.searchIcon);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search");
        EditText editText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }
}

