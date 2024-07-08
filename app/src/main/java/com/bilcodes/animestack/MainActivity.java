package com.bilcodes.animestack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bilcodes.animestack.adapter.AnimeRecyclerAdapter;
import com.bilcodes.animestack.broadcast.NetworkChangeReceiver;
import com.bilcodes.animestack.model.AnimeModel;
import com.bilcodes.animestack.networking.Networking;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    static List<AnimeModel> animeModelList;
    AnimeRecyclerAdapter animeRecyclerAdapter;

    static RecyclerView recyclerView;
    static ProgressBar progressBar;
    NestedScrollView nestedScrollView;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;

    static LinearLayout sortingOptionsArea;

    FloatingActionButton floatingActionButton;

    ImageView watchListButton;
    static ImageView errorImage;


    static Networking networking;

    MaterialCardView allButton, topRatedButton, popularButton, favoritesButton, moviesButton, mostWatchedButton;

    SharedPreferences sharedPreferences;

    public static int page = 0;
    //total pages
    public static int pageLimit = 17000;

    public static String sort = "all";

    static boolean isSearching = false;

    public static boolean isNetworkAvailable;


    BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();
    IntentFilter intentFilter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e("", Objects.requireNonNull(e.getMessage()));
        }

        page = 0;
        sort = "all";
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e("", Objects.requireNonNull(e.getMessage()));
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            }
        } catch (Exception e) {
            Log.e("", Objects.requireNonNull(e.getMessage()));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }


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


        errorImage = findViewById(R.id.error_image);
        floatingActionButton.setVisibility(View.GONE);


        networking = new Networking(this, progressBar, animeRecyclerAdapter, sortingOptionsArea, recyclerView, animeModelList, errorImage);


        allButton = findViewById(R.id.custom1);
        TextView all = allButton.findViewById(R.id.card_text);
        all.setText(R.string.all);
        topRatedButton = findViewById(R.id.custom2);
        TextView topRated = topRatedButton.findViewById(R.id.card_text);
        topRated.setText(R.string.top_rated);
        popularButton = findViewById(R.id.custom3);
        TextView popular = popularButton.findViewById(R.id.card_text);
        popular.setText(R.string.popular);
        favoritesButton = findViewById(R.id.custom4);
        TextView favorites = favoritesButton.findViewById(R.id.card_text);
        favorites.setText(R.string.favorites);
        moviesButton = findViewById(R.id.custom5);
        TextView movies = moviesButton.findViewById(R.id.card_text);
        movies.setText(R.string.movies);
        mostWatchedButton = findViewById(R.id.custom6);
        TextView mostWatched = mostWatchedButton.findViewById(R.id.card_text);
        mostWatched.setText(R.string.most_watched_shows);

        //custom tool/appbar bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (firstOpening == 0) {
//            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.watchlist_toolbar), "Watch Later", "Long Press On Desired Anime To Add Into Your Watch Later List")
//                    .cancelable(true)
//                    .transparentTarget(true));
            sharedPrefEditor.putInt("first_opening", 1);
            sharedPrefEditor.apply();
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

        watchListButton.setOnClickListener(V -> startActivity(new Intent(this, WatchlistActivity.class)));

        //drawer navigation view items controls
        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.watchlist) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, WatchlistActivity.class));
            }else if (item.getItemId() == R.id.chat) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, StackBotChatActivity.class));
            }
            else if (item.getItemId() == R.id.share) {
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
//        networking.getAllData(page, pageLimit, sort);

        showData(isNetworkAvailable);

        changeButton(allButton, topRatedButton, popularButton, favoritesButton, moviesButton, mostWatchedButton);


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

                if (!isSearching) {
                    page = page + 10;
                    progressBar.setVisibility(View.VISIBLE);
                    networking.getAllData(page, pageLimit, sort);
                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        //go to top of screen
        floatingActionButton.setOnClickListener(v -> {
            nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
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

    public static void showData(boolean isNetworkAvailable) {

        if (isNetworkAvailable) {
            if (animeModelList.isEmpty() && !isSearching) {
                networking.getAllData(page, pageLimit, sort);
            } else if (!animeModelList.isEmpty() && !isSearching) {
                sortingOptionsArea.setVisibility(View.VISIBLE);
            }
            errorImage.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            errorImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            sortingOptionsArea.setVisibility(View.GONE);
            recyclerView.setVisibility(View.INVISIBLE);


        }
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


        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                try {
                    animeModelList.clear();
                    networking.updateData();
                    sortingOptionsArea.setVisibility(View.GONE);
                    isSearching = true;
                    progressBar.setVisibility(View.GONE);
                    return true;
                } catch (Exception e) {
                    Log.e("", Objects.requireNonNull(e.getMessage()));
                }

                return false;

            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                isSearching = false;
                animeModelList.clear();
                page = 0;
                networking.updateData();
                networking.getAllData(page, pageLimit, sort);
                sortingOptionsArea.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                return true;
            }
        });


        //search functions
        SearchView searchView = (SearchView) item.getActionView();
        assert searchView != null;
        searchView.setQueryHint("Search");
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                networking.getSearchData(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


}

