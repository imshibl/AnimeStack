package com.example.animelistapp.networking;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.animelistapp.MainActivity;
import com.example.animelistapp.adapter.AnimeRecyclerAdapter;
import com.example.animelistapp.model.AnimeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Networking {


    Context mContext;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    AnimeRecyclerAdapter animeRecyclerAdapter;
    LinearLayout linearLayout;
    List<AnimeModel> animeModelList;

    String url;
    String title;
    String coverImage;

    public Networking(Context mContext, ProgressBar progressBar, AnimeRecyclerAdapter animeRecyclerAdapter, LinearLayout linearLayout, RecyclerView recyclerView, List<AnimeModel> animeModelList) {
        this.mContext = mContext;
        this.progressBar = progressBar;
        this.animeRecyclerAdapter = animeRecyclerAdapter;
        this.linearLayout = linearLayout;
        this.recyclerView = recyclerView;
        this.animeModelList = animeModelList;
    }

    public void getAllData(int pageNum, int limit, String sort) {
        if (pageNum > limit) {
            Toast.makeText(mContext, "No More Data Available", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String BASE_URL = "https://kitsu.io/api/edge/anime";

        if (sort.equals("all")) {
            url = BASE_URL + "?page[offset]=" + pageNum;
        } else if (sort.equals("top_rated")) {
            url = BASE_URL + "?sort=ratingRank&page[offset]=" + pageNum;
        } else if (sort.equals("popular")) {
            url = BASE_URL + "?sort=popularityRank&page[offset]=" + pageNum;
        } else if (sort.equals("favorites")) {
            url = BASE_URL + "?sort=-favoritesCount&page[offset]=" + pageNum;
        } else if (sort.equals("movies")) {
            url = BASE_URL + "?filter[subtype]=movie&sort=-userCount&page[offset]=" + pageNum;
        }else if(sort.equals("most_watched")){
            url = BASE_URL + "?filter[subtype]=tv&sort=-userCount&page[offset]=" + pageNum;
        }


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data = obj.getJSONArray("data");


                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String rating = object.getJSONObject("attributes").getString("averageRating");
                        String description = object.getJSONObject("attributes").getString("description");
                        String posterImage = object.getJSONObject("attributes").getJSONObject("posterImage").getString("small");



                        String subType = object.getJSONObject("attributes").getString("subtype");
                        String ageRating = object.getJSONObject("attributes").getString("ageRating");
                        String status = object.getJSONObject("attributes").getString("status");



                       try{
                           title = object.getJSONObject("attributes").getJSONObject("titles").getString("en");
                           coverImage = object.getJSONObject("attributes").getJSONObject("coverImage").getString("original");
                           if(title.equals("")){
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
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }

    public void updateData(){
        animeRecyclerAdapter.notifyDataSetChanged();
    }
}
