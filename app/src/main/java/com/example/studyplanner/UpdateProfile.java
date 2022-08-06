package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.preference.PowerPreference;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends Base {

    private static final String TAG = "Log - AddUserDetails";

    LayoutInflater inflater;
    EditText curYearUserUpdateET, curSemesterUserUpdateET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_update_profile,null,false);
        drawerLayout.addView(contentView,0);

        curYearUserUpdateET=findViewById(R.id.curYearUserUpdateET);
        curSemesterUserUpdateET=findViewById(R.id.curSemesterUserUpdateET);

        getSupportActionBar().setTitle("Add User Details");

        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year=curYearUserUpdateET.getText().toString();
                String semester=curSemesterUserUpdateET.getText().toString();

                if (year.isEmpty()){
                    curYearUserUpdateET.setError("Year required");
                    return;
                }
                if (semester.isEmpty()){
                    curSemesterUserUpdateET.setError("Semester required");
                    return;
                }

                PowerPreference.getDefaultFile().putString("CurrentYear",year);
                PowerPreference.getDefaultFile().putString("CurrentSemester",semester);

                DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid()).collection("Data").document("Profile");
                Map<String,Object> user=new HashMap<>();
                user.put("CurrentYear",year);
                user.put("CurrentSemester",semester);
                documentReference.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"User Details Updated");
                        startActivity(new Intent(getApplicationContext(),ReminderList.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "User Update Error");
                    }
                });
            }
        });
    }
}