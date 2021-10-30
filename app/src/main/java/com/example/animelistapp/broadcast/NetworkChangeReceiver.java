package com.example.animelistapp.broadcast;

import static com.example.animelistapp.MainActivity.showData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            checkConnectivity(context);
        }

    }

    private void checkConnectivity(Context context) {
        try {
            if (isNetworkAvailable(context)) {



                showData(true);


                Log.d("myapp", "connected");

//                isNetworkAvailable = true;


                showData(true);


            } else {
                showData(false);

//                isNetworkAvailable = false;

                showData(false);
                Log.d("myapp", "not connected");
                Toast.makeText(context, "Network not available,", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }


}
