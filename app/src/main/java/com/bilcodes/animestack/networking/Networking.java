package com.bilcodes.animestack.networking;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bilcodes.animestack.adapter.AnimeRecyclerAdapter;
import com.bilcodes.animestack.model.AnimeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class Networking {


    Context mContext;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    AnimeRecyclerAdapter animeRecyclerAdapter;
    LinearLayout linearLayout;
    List<AnimeModel> animeModelList;
    ImageView errorImage;

    String url;
    String title;
    String coverImage;
    String rating;
    String description;
    String posterImage;



    String subType;
    String ageRating;
    String status;

    final String BASE_URL = "https://kitsu.io/api/edge/anime";

    public Networking(Context mContext, ProgressBar progressBar, AnimeRecyclerAdapter animeRecyclerAdapter, LinearLayout linearLayout, RecyclerView recyclerView, List<AnimeModel> animeModelList, ImageView errorImage) {
        this.mContext = mContext;
        this.progressBar = progressBar;
        this.animeRecyclerAdapter = animeRecyclerAdapter;
        this.linearLayout = linearLayout;
        this.recyclerView = recyclerView;
        this.animeModelList = animeModelList;
        this.errorImage = errorImage;
    }

    public void getAllData(int pageNum, int limit, String sort) {
        if (pageNum > limit) {
            Toast.makeText(mContext, "No More Data Available", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }


        switch (sort) {
            case "all":
                url = BASE_URL + "?page[offset]=" + pageNum;
                break;
            case "top_rated":
                url = BASE_URL + "?sort=ratingRank&page[offset]=" + pageNum;
                break;
            case "popular":
                url = BASE_URL + "?sort=popularityRank&page[offset]=" + pageNum;
                break;
            case "favorites":
                url = BASE_URL + "?sort=-favoritesCount&page[offset]=" + pageNum;
                break;
            case "movies":
                url = BASE_URL + "?filter[subtype]=movie&sort=-userCount&page[offset]=" + pageNum;
                break;
            case "most_watched":
                url = BASE_URL + "?filter[subtype]=tv&sort=-userCount&page[offset]=" + pageNum;
                break;
        }


        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray data = obj.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    rating = object.getJSONObject("attributes").getString("averageRating");
                    description = object.getJSONObject("attributes").getString("description");
                    posterImage = object.getJSONObject("attributes").getJSONObject("posterImage").getString("small");



                    subType = object.getJSONObject("attributes").getString("subtype");
                    ageRating = object.getJSONObject("attributes").getString("ageRating");
                    status = object.getJSONObject("attributes").getString("status");



                   try{
                       title = object.getJSONObject("attributes").getJSONObject("titles").getString("en");
                       coverImage = object.getJSONObject("attributes").getJSONObject("coverImage").getString("original");
                       if(title.isEmpty()){
                           title = object.getJSONObject("attributes").getJSONObject("titles").getString("en_jp");
                       }
                   } catch (Exception e){
                       coverImage = null;
                       try{
                           title = object.getJSONObject("attributes").getJSONObject("titles").getString("en_jp");
                       }catch (Exception e1){
                           title = object.getJSONObject("attributes").getString("slug");
                       }

                   }


                    if (rating.equals("null")){
                        rating = "0";
                    }



                    AnimeModel model = new AnimeModel(title, description, posterImage, rating, subType, ageRating, status, coverImage);
                    animeModelList.add(model);


                }

                animeRecyclerAdapter = new AnimeRecyclerAdapter(mContext, animeModelList);
                recyclerView.setAdapter(animeRecyclerAdapter);
                linearLayout.setVisibility(View.VISIBLE);



            } catch (JSONException e) {
                Log.e("", Objects.requireNonNull(e.getMessage()));
            }

        }, error -> Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }

    public void updateData(){
        animeRecyclerAdapter.notifyDataSetChanged();
    }

    public void getSearchData(String search) {

        if(search.isEmpty()){
            url = BASE_URL + "?sort=ratingRank";
        }

        animeModelList.clear();



        try {
            url = BASE_URL + "?filter[text]=" + search + "&page[limit]=20";
        }catch (Exception e){
            Log.e("", Objects.requireNonNull(e.getMessage()));

        }





        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray data = obj.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    rating = object.getJSONObject("attributes").getString("averageRating");
                    description = object.getJSONObject("attributes").getString("description");
                    posterImage = object.getJSONObject("attributes").getJSONObject("posterImage").getString("small");



                    subType = object.getJSONObject("attributes").getString("subtype");
                    ageRating = object.getJSONObject("attributes").getString("ageRating");
                    status = object.getJSONObject("attributes").getString("status");



                    try{
                        title = object.getJSONObject("attributes").getJSONObject("titles").getString("en");
                        coverImage = object.getJSONObject("attributes").getJSONObject("coverImage").getString("original");
                        if(title.isEmpty()){
                            title = object.getJSONObject("attributes").getJSONObject("titles").getString("en_jp");
                        }
                    } catch (Exception e){
                        coverImage = null;
                        try{
                            title = object.getJSONObject("attributes").getJSONObject("titles").getString("en_jp");
                        }catch (Exception e1){
                            title = object.getJSONObject("attributes").getString("slug");
                        }

                    }


                    if (rating.equals("null")){
                        rating = "0";
                    }



                    AnimeModel model = new AnimeModel(title, description, posterImage, rating, subType, ageRating, status, coverImage);
                    animeModelList.add(model);


                }

                animeRecyclerAdapter = new AnimeRecyclerAdapter(mContext, animeModelList);
                recyclerView.setAdapter(animeRecyclerAdapter);




            } catch (JSONException e) {
                Log.e("", Objects.requireNonNull(e.getMessage()));

            }

        }, error -> {
            Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            animeModelList.clear();
            updateData();

        });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }
}
