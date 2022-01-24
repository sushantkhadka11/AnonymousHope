package com.sushant.anonymoushope.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sushant.anonymoushope.MainActivity;
import com.sushant.anonymoushope.R;

public class SplashScreenActivity extends AppCompatActivity {

    TextView textView,tryAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textView = findViewById(R.id.noInternet);
        tryAgain = findViewById(R.id.tryAgain);

        final boolean wifi = checkConnection();
        refresh(wifi);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SplashScreenActivity.this, "Checking connection again", Toast.LENGTH_SHORT).show();
                boolean checkConnection = checkConnection();
                refresh(checkConnection);

            }
        });
    }
    private void refresh(boolean wifi)
    {
        if(wifi)
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);
        }
        else
        {
            textView.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
            });
            Toast.makeText(this, "No connection found", Toast.LENGTH_SHORT).show();

        }
    }
    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)

                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo network = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (wifi.isConnected()) {

            return true;

        }
        else return network.isConnected();

    }
}