package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LessonRecord extends Base {
    LayoutInflater inflater;
    Spinner codeLesRecordS;
    TextView dateLesRecordTV;
    RecyclerView lessonRecordRecV;

    SimpleDateFormat docDateFormat=new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    SimpleDateFormat dateFormat=new SimpleDateFormat( "EEE, dd MMM yyyy", Locale.getDefault());

    FirestorePagingAdapter<LessonModel, LessonViewHolder> lessonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_lesson_record,null,false);
        drawerLayout.addView(contentView,0);

        codeLesRecordS=findViewById(R.id.codeLesRecordS);
        dateLesRecordTV=findViewById(R.id.dateLesRecordTV);
        lessonRecordRecV=findViewById(R.id.lessonRecordRecV);

        getSupportActionBar().setTitle("Lesson Records");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24,null));
        }

        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLesRecordDialog();
            }
        });

        Query query2=fStore.collection("NonShared").document(fUser.getUid()).collection("Lessons")
                .orderBy("Date");
        recV(query2);

        List<String> courses=new ArrayList<>();
        ArrayAdapter<String> courseAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codeLesRecordS.setAdapter(courseAdapter);

        Query query=fStore.collection("NonShared").document(fAuth.getUid()).collection("CourseList")
                .document(currentYearPref).collection(currentSemesterPref);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    courses.add("All");
                    for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                        String course=queryDocumentSnapshot.getString("Code");
                        courses.add(course);
                    }
                    courseAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(LessonRecord.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        codeLesRecordS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (codeLesRecordS.getSelectedItem().toString().equals("All")){
                    Query query2=fStore.collection("NonShared").document(fUser.getUid()).collection("Lessons")
                            .orderBy("Date");
                    recV(query2);
                }
                else {
                    Toast.makeText(LessonRecord.this, codeLesRecordS.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    Query query1=fStore.collection("NonShared").document(fUser.getUid()).collection("Lessons")
                            .whereEqualTo("CourseCode",codeLesRecordS.getSelectedItem().toString()).orderBy("Date");
                    recV(query1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void recV(Query query) {
        PagingConfig config=new PagingConfig(
                20,10,false);

        FirestorePagingOptions<LessonModel> options = new FirestorePagingOptions.Builder<LessonModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, LessonModel.class)
                .build();

        lessonAdapter=new FirestorePagingAdapter<LessonModel, LessonViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LessonViewHolder holder, int position, @NonNull LessonModel model) {
                holder.lessonCodeItemTV.setText(model.getCourseCode());
                String date=model.getDate();
                Calendar calendar=Calendar.getInstance();
                calendar.set(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4,6)),Integer.parseInt(date.substring(6,8)));
                holder.lessonDateItemTV.setText(dateFormat.format(calendar.getTime()));
                holder.lessonTopicsItemTV.setText(model.getTopics());
            }

            @NonNull
            @Override
            public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lesson,parent,false);
                return new LessonViewHolder(view);
            }
        };

        lessonRecordRecV.setHasFixedSize(true);
        lessonRecordRecV.setLayoutManager(new LinearLayoutManager(this));
        lessonRecordRecV.setAdapter(lessonAdapter);
    }

    private void addLesRecordDialog() {
        Dialog dialog=new Dialog(LessonRecord.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_lesson_record);

        Calendar calendar=Calendar.getInstance();

        Spinner courseCodeAddLesRecordS=dialog.findViewById(R.id.courseCodeAddLesRecordS);
        TextView dateAddLesRecordTV=dialog.findViewById(R.id.dateAddLesRecordTV);
        EditText topicsAddLesRecordET=dialog.findViewById(R.id.topicsAddLesRecordET);
        Button saveAddLesRecordB=dialog.findViewById(R.id.saveAddLesRecordB);

        List<String> courses=new ArrayList<>();
        ArrayAdapter<String> courseAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseCodeAddLesRecordS.setAdapter(courseAdapter);

        Query query=fStore.collection("NonShared").document(fAuth.getUid()).collection("CourseList")
                .document(currentYearPref).collection(currentSemesterPref);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                        String course=queryDocumentSnapshot.getString("Code");
                        courses.add(course);
                    }
                    courseAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(LessonRecord.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        courseCodeAddLesRecordS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateAddLesRecordTV.setText(dateFormat.format(calendar.getTime()));

        dateAddLesRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LessonRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        dateAddLesRecordTV.setText(dateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        saveAddLesRecordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topics=topicsAddLesRecordET.getText().toString();
                if (topics.isEmpty()){
                    topicsAddLesRecordET.setError("Enter topics please");
                    return;
                }
                String courseCode=courseCodeAddLesRecordS.getSelectedItem().toString();
                DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                        .collection("Lessons").document(courseCode+docDateFormat.format(calendar.getTime()));
                Map<String,Object> topic=new HashMap<>();
                topic.put("CourseCode",courseCode);
                topic.put("Date",docDateFormat.format(calendar.getTime()));
                topic.put("Topics",topics);
                documentReference.set(topic, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LessonRecord.this, "Lesson added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LessonRecord.this, "Lesson failed", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        dialog.show();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder{
        TextView lessonCodeItemTV, lessonDateItemTV, lessonTopicsItemTV;
        View view;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonCodeItemTV=itemView.findViewById(R.id.lessonCodeItemTV);
            lessonDateItemTV=itemView.findViewById(R.id.lessonDateItemTV);
            lessonTopicsItemTV=itemView.findViewById(R.id.lessonTopicsItemTV);
            view=itemView;
        }
    }
}