package com.bilcodes.animestack.broadcast;

import static com.bilcodes.animestack.MainActivity.showData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

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
            Log.e("", Objects.requireNonNull(e.getMessage()));
        }
    }

    public boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("", Objects.requireNonNull(e.getMessage()));
            return false;
        }


    }


}
