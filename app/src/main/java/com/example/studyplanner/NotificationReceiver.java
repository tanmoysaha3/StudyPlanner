package com.example.studyplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.preference.PowerPreference;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    String currentEvent= PowerPreference.getDefaultFile().getString("CurrentEvent");
    int id=Integer.parseInt(currentEvent.substring(currentEvent.length()-6));
    String channelId="StudyPlanner";

    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
    Calendar calendar=Calendar.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification= new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(intent.getStringExtra("titleExtra"))
                .setContentText(intent.getStringExtra("messageExtra"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);

        Query query=fStore.collection("NonShared").document(fUser.getUid()).collection("Events")
                .whereGreaterThan("NotificationId",currentEvent).orderBy("NotificationId").limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot=task.getResult();
                    if (querySnapshot.size()==0){
                        PowerPreference.getDefaultFile().putString("CurrentEvent","0");
                    }
                    else {
                        DocumentSnapshot documentSnapshot=querySnapshot.getDocuments().get(0);
                        String notfId=documentSnapshot.getString("NotificationId");
                        PowerPreference.getDefaultFile().putString("CurrentEvent",notfId);
                        String courseName=documentSnapshot.getString("CourseName");
                        String eventType=documentSnapshot.getString("EventType");
                        String startTime=documentSnapshot.getString("StartTime");
                        String endTime=documentSnapshot.getString("EndTime");

                        int year=Integer.parseInt(notfId.substring(0,4));
                        int month=Integer.parseInt(notfId.substring(4,6));
                        int date=Integer.parseInt(notfId.substring(6,8));
                        int hour=Integer.parseInt(notfId.substring(8,10));
                        int minute=Integer.parseInt(notfId.substring(10,12));
                        int second=Integer.parseInt(notfId.substring(12,14));

                        calendar.set(year,month-1,date,hour,minute,second);
                        long time=calendar.getTimeInMillis();

                        Intent intent = new Intent(context, NotificationReceiver.class);
                        intent.putExtra("titleExtra",courseName+eventType);
                        intent.putExtra("messageExtra",startTime+" - "+endTime);
                        int value = Integer.parseInt(notfId.substring(notfId.length() - 6));
                        intent.putExtra("notfIdExtra", value);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, value, intent, PendingIntent.FLAG_ONE_SHOT);

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                time,
                                pendingIntent
                        );
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
