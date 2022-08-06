package com.example.studyplanner;

import androidx.annotation.NonNull;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCourse extends Base {
    LayoutInflater inflater;
    EditText codeAddCourseET, titleAddCourseET, creditAddCourseET;
    TextView fNameAddCourseTV;
    ImageButton sFileAddCourseIB;
    LinearLayout topicAddCourseLL, insAddCourseLL, bookAddCourseLL;

    FirebaseFirestore fStore;

    int topicNo=0, insNo=0, bookNo=0;
    List<String> topicList = new ArrayList<>();
    List<String> insList = new ArrayList<>();
    List<String> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_add_course,null,false);
        drawerLayout.addView(contentView,0);

        codeAddCourseET=findViewById(R.id.codeAddCourseET);
        titleAddCourseET=findViewById(R.id.titleAddCourseET);
        creditAddCourseET=findViewById(R.id.creditAddCourseET);
        fNameAddCourseTV=findViewById(R.id.fNameAddCourseTV);
        sFileAddCourseIB=findViewById(R.id.sFileAddCourseIB);
        topicAddCourseLL=findViewById(R.id.topicAddCourseLL);
        insAddCourseLL=findViewById(R.id.insAddCourseLL);
        bookAddCourseLL=findViewById(R.id.bookAddCourseLL);

        fStore= FirebaseFirestore.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Course");

        addTopic();
        addIns();
        addBook();

        sFileAddCourseIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCourse.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int topicCount = topicAddCourseLL.getChildCount();
                for (int i=0; i < topicCount-1; i++){
                    View view=topicAddCourseLL.getChildAt(i);
                    EditText editText=view.findViewById(i);
                    String topic=editText.getText().toString();
                    topicList.add(topic);
                }

                int insCount = insAddCourseLL.getChildCount();
                for (int i=0; i < insCount-1; i++){
                    View view = insAddCourseLL.getChildAt(i);
                    EditText editText=view.findViewById(i);
                    String ins=editText.getText().toString();
                    insList.add(ins);
                }

                int bookCount = bookAddCourseLL.getChildCount();
                for (int i=0; i <bookCount-1; i++){
                    View view = bookAddCourseLL.getChildAt(i);
                    EditText editText=view.findViewById(i);
                    String book=editText.getText().toString();
                    bookList.add(book);
                }

                String code=codeAddCourseET.getText().toString();
                String title=titleAddCourseET.getText().toString();
                String credit=creditAddCourseET.getText().toString();
                String type;
                if (code.isEmpty()){
                    codeAddCourseET.setError("Required");
                    return;
                }
                if (!code.substring(code.length()-4).matches("[0-9]+")){
                    codeAddCourseET.setError("Enter Correct Course Code");
                    return;
                }

                String courseTypeCheck=code.substring(code.length() - 1);
                if (Integer.parseInt(courseTypeCheck)%2==0){
                    type="Lab";
                }
                else {
                    type="Written";
                }

                DocumentReference documentReference=fStore.collection("NonShared").document(fAuth.getUid())
                        .collection("CourseList").document(currentYearPref).collection(currentSemesterPref).document(code);
                Map<String,Object> course=new HashMap<>();
                course.put("Code",code);
                course.put("Title",title);
                course.put("Credit",credit);
                course.put("Type",type);
                course.put("TotalTopic",topicCount-1);
                course.put("CompletedTopic",0);
                //course.put("Topics",topicList);
                course.put("Instructors",insList);
                course.put("Books",bookList);
                documentReference.set(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddCourse.this, "Course Added", Toast.LENGTH_SHORT).show();
                        for (int i=0;i<topicCount-1;i++){
                            DocumentReference documentReference1=fStore.collection("NonShared").document(fAuth.getUid())
                                    .collection("CourseList").document(currentYearPref).collection(currentSemesterPref)
                                    .document(code).collection("Topics").document(topicList.get(i));
                            Map<String,Object> topic=new HashMap<>();
                            topic.put("TopicName", topicList.get(i));
                            topic.put("IsCompleted","False");
                            int finalI = i;
                            documentReference1.set(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddCourse.this, "Topic "+topicList.get(finalI)+" added", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCourse.this, "Topic addition failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCourse.this, "Course Addition Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private void addTopic() {
        EditText topicNew= (EditText) getLayoutInflater().inflate(R.layout.custom_edittext,topicAddCourseLL,false);
        topicNew.setHint("Course Topic");
        topicNew.setId(topicNo);
        topicAddCourseLL.addView(topicNew);

        topicNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && topicAddCourseLL.getChildCount() > 1) {
                    topicNo--;
                    topicAddCourseLL.removeView(topicNew);
                } else if (s.length() > 0 && ((before + start) == 0)) {
                    topicNo++;
                    addTopic();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addIns() {
        EditText insNew= (EditText) getLayoutInflater().inflate(R.layout.custom_edittext,insAddCourseLL,false);
        insNew.setHint("Course Instructor");
        insNew.setId(insNo);
        insAddCourseLL.addView(insNew);

        insNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && insAddCourseLL.getChildCount() > 1) {
                    insNo--;
                    insAddCourseLL.removeView(insNew);
                } else if (s.length() > 0 && ((before + start) == 0)) {
                    insNo++;
                    addIns();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addBook() {
        EditText bookNew= (EditText) getLayoutInflater().inflate(R.layout.custom_edittext,bookAddCourseLL,false);
        bookNew.setHint("Course Book");
        bookNew.setId(bookNo);
        bookAddCourseLL.addView(bookNew);

        bookNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && bookAddCourseLL.getChildCount() > 1) {
                    bookNo--;
                    bookAddCourseLL.removeView(bookNew);
                } else if (s.length() > 0 && ((before + start) == 0)) {
                    bookNo++;
                    addBook();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}