package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.preference.PowerPreference;

public class Base extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar mainToolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;
    String currentYearPref, currentSemesterPref;
    ImageButton mainSaveIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mainToolbar=findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));

        mainSaveIB=findViewById(R.id.mainSaveIB);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();

        navView=findViewById(R.id.navView);
        drawerLayout=findViewById(R.id.drawerLayout);

        navView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,mainToolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        PowerPreference.init(this);
        currentYearPref=PowerPreference.getDefaultFile().getString("CurrentYear");
        currentSemesterPref=PowerPreference.getDefaultFile().getString("CurrentSemester");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.calNavM:
                startActivity(new Intent(getApplicationContext(),ReminderList.class));
                finish();
                break;
            case R.id.courseListNavM:
                startActivity(new Intent(getApplicationContext(), CourseList.class));
                finish();
                break;
            case R.id.profileNavM:
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
                finish();
                break;
            case R.id.impvmtNavM:
                startActivity(new Intent(getApplicationContext(),Improvement.class));
                finish();
                break;
            case R.id.lesRecordNavM:
                startActivity(new Intent(getApplicationContext(),LessonRecord.class));
                finish();
                break;
            case R.id.resultNavM:
                startActivity(new Intent(getApplicationContext(),ResultRecord.class));
                finish();
                break;
            case R.id.logOutNavM:
                fAuth.signOut();
                PowerPreference.getDefaultFile().clearAsync();
                startActivity(new Intent(getApplication(), Login.class));
                finish();
                break;
        }
        return false;
    }
}