package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class CourseList extends Base {

    private static final String TAG = "Log - CourseList";

    LayoutInflater inflater;
    RecyclerView courseListRecV;
    FirestorePagingAdapter<CourseModel, CourseViewHolder> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_course_list,null,false);
        drawerLayout.addView(contentView,0);

        getSupportActionBar().setTitle("Course List");

        courseListRecV=findViewById(R.id.courseListRecV);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24,null));
        }
        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddCourse.class));
            }
        });

        Query query=fStore.collection("NonShared").document(fAuth.getUid())
                .collection("CourseList").document(currentYearPref).collection(currentSemesterPref);

        PagingConfig config=new PagingConfig(
                20,10,false);

        FirestorePagingOptions<CourseModel> options = new FirestorePagingOptions.Builder<CourseModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, CourseModel.class)
                .build();

        courseAdapter=new FirestorePagingAdapter<CourseModel, CourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull CourseModel model) {
                holder.courseCodeItemTV.setText(model.getCode());
                holder.courseCodeItemTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CourseList.this, model.getCode(), Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), CourseDetails.class);
                        intent.putExtra("CourseCode",model.getCode());
                        startActivity(intent);
                    }
                });
                holder.deleteCourseItemIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"Delete Clicked");
                    }
                });
            }

            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_course,parent,false);
                return new CourseViewHolder(view);
            }
        };

        courseListRecV.setHasFixedSize(true);
        courseListRecV.setLayoutManager(new LinearLayoutManager(this));
        courseListRecV.setAdapter(courseAdapter);
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseCodeItemTV;
        ImageButton deleteCourseItemIB;
        View view;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeItemTV=itemView.findViewById(R.id.courseCodeItemTV);
            deleteCourseItemIB=itemView.findViewById(R.id.courseDeleteItemIB);
            view=itemView;
        }
    }
}