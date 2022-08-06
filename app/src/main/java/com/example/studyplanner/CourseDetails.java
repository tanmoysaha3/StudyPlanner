package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.preference.PowerPreference;

import java.util.HashMap;
import java.util.Map;

public class CourseDetails extends Base {

    private static final String TAG = "Log - CourseDetails";
    LayoutInflater inflater;

    TextView codeCourseDetailsTV, titleCourseDetailsTV, creditCourseDetailsTV;
    RecyclerView topicCodeDetailsRecV;
    FirestorePagingAdapter<TopicModel, TopicViewHolder> topicAdapter;
    String status;
    int statusValue;

    CircularProgressBar courseDetailsCPBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_course_details,null,false);
        drawerLayout.addView(contentView,0);

        codeCourseDetailsTV=findViewById(R.id.codeCourseDetailsTV);
        titleCourseDetailsTV=findViewById(R.id.titleCourseDetailsTV);
        creditCourseDetailsTV=findViewById(R.id.creditCourseDetailsTV);
        topicCodeDetailsRecV=findViewById(R.id.topicCourseSetailsRecV);
        courseDetailsCPBar=findViewById(R.id.courseDetailsCPBar);

        Intent data=getIntent();
        String intentCode=data.getStringExtra("CourseCode");

        PowerPreference.showDebugScreen(true);
        DocumentReference documentReference=fStore.collection("NonShared").document(fAuth.getUid())
                .collection("CourseList").document(currentYearPref).collection(currentSemesterPref).document(intentCode);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    codeCourseDetailsTV.setText(documentSnapshot.getString("Code"));
                    titleCourseDetailsTV.setText(documentSnapshot.getString("Title"));
                    creditCourseDetailsTV.setText(documentSnapshot.getString("Credit"));
                    courseDetailsCPBar.setProgress(documentSnapshot.getLong("CompletedTopic"));
                    courseDetailsCPBar.setProgressMax(documentSnapshot.getLong("TotalTopic"));
                    courseDetailsCPBar.setProgressBarWidth(14f); // in DP
                    courseDetailsCPBar.setBackgroundProgressBarWidth(10f); // in DP
                }
                else {
                    Toast.makeText(CourseDetails.this, "Null", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Query query=fStore.collection("NonShared").document(fAuth.getUid())
                .collection("CourseList").document(currentYearPref).collection(currentSemesterPref)
                .document(intentCode).collection("Topics");

        PagingConfig config=new PagingConfig(
                20,10,false);

        FirestorePagingOptions<TopicModel> options = new FirestorePagingOptions.Builder<TopicModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, TopicModel.class)
                .build();

        topicAdapter=new FirestorePagingAdapter<TopicModel, TopicViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TopicViewHolder holder, int position, @NonNull TopicModel model) {
                holder.topicNameItemTV.setText(model.getTopicName());
                holder.topicStatusItemTV.setText(model.getIsCompleted());

                holder.topicStatusItemTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(CourseDetails.this, model.getTopicName(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(CourseDetails.this, model.getIsCompleted(), Toast.LENGTH_SHORT).show();
                        if (model.getIsCompleted().equals("False")){
                            status="True";
                            statusValue=1;
                        }
                        else if (model.getIsCompleted().equals("True")){
                            status="False";
                            statusValue=-1;
                        }
                        DocumentReference documentReference1=fStore.collection("NonShared").document(fAuth.getUid())
                                .collection("CourseList").document(currentYearPref).collection(currentSemesterPref)
                                .document(intentCode).collection("Topics").document(model.getTopicName());
                        Map<String,Object> topic=new HashMap<>();
                        topic.put("IsCompleted",status);
                        documentReference1.update(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CourseDetails.this, "Topic status updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CourseDetails.this, "Topic update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        DocumentReference documentReference2=fStore.collection("NonShared").document(fAuth.getUid())
                                .collection("CourseList").document(currentYearPref).collection(currentSemesterPref)
                                .document(intentCode);
                        documentReference2.update("CompletedTopic", FieldValue.increment(statusValue));
                        return true;
                    }
                });
                holder.topicDeleteItemIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"Delete Clicked");
                    }
                });
            }

            @NonNull
            @Override
            public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_topic,parent,false);
                return new TopicViewHolder(view);
            }
        };

        topicCodeDetailsRecV.setHasFixedSize(true);
        topicCodeDetailsRecV.setLayoutManager(new LinearLayoutManager(this));
        topicCodeDetailsRecV.setAdapter(topicAdapter);
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicNameItemTV, topicStatusItemTV;
        ImageButton topicDeleteItemIB;
        View view;
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicNameItemTV=itemView.findViewById(R.id.topicNameItemTV);
            topicDeleteItemIB=itemView.findViewById(R.id.topicDeleteItemIB);
            topicStatusItemTV=itemView.findViewById(R.id.topicStatusItemTV);
            view=itemView;
        }
    }
}