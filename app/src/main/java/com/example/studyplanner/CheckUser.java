package com.example.studyplanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.preference.PowerPreference;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CheckUser extends AppCompatActivity {

    private static final String TAG = "Log - CheckUser";

    ProgressBar checkUserPBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String channelId="StudyPlanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);

        checkUserPBar=findViewById(R.id.checkUserPBar);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        checkUserPBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Wait please", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Wait Please");


        PowerPreference.init(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createNotificationChannel();
                if (isOnline()){
                    if (fAuth.getCurrentUser()!=null) {
                        Log.d(TAG, "online");
                        if (PowerPreference.getDefaultFile().getString("CurrentYear","none").equals("none")){
                            startActivity(new Intent(getApplicationContext(),UpdateProfile.class));
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(),ReminderList.class));
                        }
                    }
                    else {
                        Log.d(TAG,"User is null");
                        startActivity(new Intent(getApplicationContext(),Login.class));
                    }
                    finish();
                }
                else {
                    Log.d(TAG,"User is offline");
                    offlineAlert();
                }

            }
        },2000);
    }

    private boolean isOnline(){
        try {
            int timeoutMs=1500;
            Socket socket=new Socket();
            SocketAddress socketAddress=new InetSocketAddress("8.8.8.8",53);

            socket.connect(socketAddress,timeoutMs);
            socket.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    private void offlineAlert(){
        AlertDialog.Builder warning=new AlertDialog.Builder(this)
                .setTitle("Network Error")
                .setMessage("You are not connected with internet")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                }).setNegativeButton("Browse Offline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (fAuth.getCurrentUser()!=null){
                            Log.d(TAG, "Browse Offline");
                            startActivity(new Intent(getApplicationContext(), ReminderList.class));
                            finish();
                        }
                        else {
                            Toast.makeText(CheckUser.this, "Need internet for first time use \n Connect to internet and restart app.", Toast.LENGTH_SHORT).show();
                            startActivity(getIntent());
                        }
                    }
                });
        warning.show();
    }

    private void createNotificationChannel() {
        CharSequence name = "channelName";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_MAX;
        @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}