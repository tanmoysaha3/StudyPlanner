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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultRecord extends Base {

    LayoutInflater inflater;
    RecyclerView resultRecordRecV;

    SimpleDateFormat docDateFormat=new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    SimpleDateFormat dateFormat=new SimpleDateFormat( "EEE, dd MMM yyyy", Locale.getDefault());

    FirestorePagingAdapter<ResultModel, ResultViewHolder> resultAdapter;

    String pCT1Mark, pCT2Mark, pCT3Mark, pCT1Date, pCT2Date, pCT3Date, pAssigmntMark, pAssigmntDate, pFinalMark, pFinalDate;
    String c1D,c2D, c3D, aD, fD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_result_record,null,false);
        drawerLayout.addView(contentView,0);

        resultRecordRecV=findViewById(R.id.resultRecordRecV);

        getSupportActionBar().setTitle("Result Records");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24,null));
        }

        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addResultRecordDialog();
            }
        });

        Query query=fStore.collection("NonShared").document(fUser.getUid()).collection("Results")
                .orderBy("CourseCode");
        recV(query);
    }

    private void recV(Query query) {
        PagingConfig config=new PagingConfig(
                20,10,false);

        FirestorePagingOptions<ResultModel> options = new FirestorePagingOptions.Builder<ResultModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ResultModel.class)
                .build();

        resultAdapter=new FirestorePagingAdapter<ResultModel, ResultViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ResultViewHolder holder, int position, @NonNull ResultModel model) {
                holder.resultCodeItemTV.setText(model.getCourseCode());
                holder.resultCT1MarkItemTV.setText("CT1:                 "+model.getCT1Mark());
                String ct1Date=model.getCT1Date();
                Calendar calendar=Calendar.getInstance();
                calendar.set(Integer.parseInt(ct1Date.substring(0,4)),Integer.parseInt(ct1Date.substring(4,6)),Integer.parseInt(ct1Date.substring(6,8)));
                holder.resultCT1DateItemTV.setText(dateFormat.format(calendar.getTime()));
                holder.resultCT2MarkItemTV.setText("CT2:                 "+model.getCT2Mark());
                String ct2Date=model.getCT2Date();
                if (!ct2Date.equals("Null")){
                    Calendar calendar1=Calendar.getInstance();
                    calendar1.set(Integer.parseInt(ct2Date.substring(0,4)),Integer.parseInt(ct2Date.substring(4,6)),Integer.parseInt(ct2Date.substring(6,8)));
                    holder.resultCT2DateItemTV.setText(dateFormat.format(calendar1.getTime()));
                }
                else {
                    holder.resultCT2DateItemTV.setText(ct2Date);
                }
                holder.resultCT3MarkItemTV.setText("CT3:                 "+model.getCT3Mark());
                String ct3Date=model.getCT3Date();
                if (!ct3Date.equals("Null")){
                    Calendar calendar1=Calendar.getInstance();
                    calendar1.set(Integer.parseInt(ct3Date.substring(0,4)),Integer.parseInt(ct3Date.substring(4,6)),Integer.parseInt(ct3Date.substring(6,8)));
                    holder.resultCT3DateItemTV.setText(dateFormat.format(calendar1.getTime()));
                }
                else {
                    holder.resultCT3DateItemTV.setText(ct3Date);
                }
                holder.resultAssigmntMarkItemTV.setText("Assignment:   "+model.getAssignmentMark());
                String assigmntDate=model.getAssignmentDate();
                if (!assigmntDate.equals("Null")){
                    Calendar calendar1=Calendar.getInstance();
                    calendar1.set(Integer.parseInt(assigmntDate.substring(0,4)),Integer.parseInt(assigmntDate.substring(4,6)),Integer.parseInt(assigmntDate.substring(6,8)));
                    holder.resultAssigmntDateItemTV.setText(dateFormat.format(calendar1.getTime()));
                }
                else {
                    holder.resultAssigmntDateItemTV.setText(assigmntDate);
                }
                holder.resultFinalMarkItemTV.setText("Final:                "+model.getFinalMark());
                String finalDate=model.getFinalDate();
                if (!finalDate.equals("Null")){
                    Calendar calendar1=Calendar.getInstance();
                    calendar1.set(Integer.parseInt(finalDate.substring(0,4)),Integer.parseInt(finalDate.substring(4,6)),Integer.parseInt(finalDate.substring(6,8)));
                    holder.resultFinalDateItemTV.setText(dateFormat.format(calendar1.getTime()));
                }
                else {
                    holder.resultFinalDateItemTV.setText(finalDate);
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editResultRecordDialog(model.getCourseCode());
                    }
                });
            }

            @NonNull
            @Override
            public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result,parent,false);
                return new ResultViewHolder(view);
            }
        };

        resultRecordRecV.setHasFixedSize(true);
        resultRecordRecV.setLayoutManager(new LinearLayoutManager(this));
        resultRecordRecV.setAdapter(resultAdapter);
    }

    private void editResultRecordDialog(String courseCode) {
        Dialog dialog=new Dialog(ResultRecord.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_result_record);

        Calendar calendar=Calendar.getInstance();

        Spinner courseCodeAddRsltRecordS=dialog.findViewById(R.id.courseCodeAddRsltRecordS);
        EditText ct1MarkAddRsltRecordET=dialog.findViewById(R.id.ct1MarkAddRsltRecordET);
        TextView ct1DateAddRsltRecordTV=dialog.findViewById(R.id.ct1DateAddRsltRecordTV);
        EditText ct2MarkAddRsltRecordET=dialog.findViewById(R.id.ct2MarkAddRsltRecordET);
        TextView ct2DateAddRsltRecordTV=dialog.findViewById(R.id.ct2DateAddRsltRecordTV);
        EditText ct3MarkAddRsltRecordET=dialog.findViewById(R.id.ct3MarkAddRsltRecordET);
        TextView ct3DateAddRsltRecordTV=dialog.findViewById(R.id.ct3DateAddRsltRecordTV);
        EditText assigmntMarkAddRsltRecordET=dialog.findViewById(R.id.assigmntMarkAddRsltRecordET);
        TextView assigmntDateAddRsltRecordTV=dialog.findViewById(R.id.assigmntDateAddRsltRecordTV);
        EditText finalMarkAddRsltRecordET=dialog.findViewById(R.id.finalMarkAddRsltRecordET);
        TextView finalDateAddRsltRecordTV=dialog.findViewById(R.id.finalDateAddRsltRecordTV);
        Button saveAddRsltRecordB=dialog.findViewById(R.id.saveAddRsltRecordB);



        List<String> courses=new ArrayList<>();
        ArrayAdapter<String> courseAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseCodeAddRsltRecordS.setAdapter(courseAdapter);

        DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                .collection("Results").document(courseCode);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        pCT1Mark=documentSnapshot.getString("CT1Mark");
                        pCT1Date=documentSnapshot.getString("CT1Date");
                        pCT2Mark=documentSnapshot.getString("CT2Mark");
                        pCT2Date=documentSnapshot.getString("CT2Date");
                        pCT3Mark=documentSnapshot.getString("CT3Mark");
                        pCT3Date=documentSnapshot.getString("CT3Date");
                        pAssigmntMark=documentSnapshot.getString("AssignmentMark");
                        pAssigmntDate=documentSnapshot.getString("AssignmentDate");
                        pFinalMark=documentSnapshot.getString("FinalMark");
                        pFinalDate=documentSnapshot.getString("FinalDate");

                        Toast.makeText(ResultRecord.this, "PC1M"+pCT1Mark+"PC1D"+pCT1Date+"PC2M"+pCT2Mark+
                                "PC2D"+pCT2Date+"PC3M"+pCT3Mark+"PC3D"+pCT3Date, Toast.LENGTH_SHORT).show();

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
                                    courseCodeAddRsltRecordS.setSelection(courseAdapter.getPosition(courseCode));
                                }
                                else {
                                    Toast.makeText(ResultRecord.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        courseCodeAddRsltRecordS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        ct1MarkAddRsltRecordET.setText(pCT1Mark);
                        if (!pCT1Date.equals("Null")){
                            Calendar calendar1=Calendar.getInstance();
                            calendar1.set(Integer.parseInt(pCT1Date.substring(0,4)),Integer.parseInt(pCT1Date.substring(4,6)),Integer.parseInt(pCT1Date.substring(6,8)));
                            ct1DateAddRsltRecordTV.setText(dateFormat.format(calendar1.getTime()));
                        }
                        else {
                            ct1DateAddRsltRecordTV.setText(pCT1Date);
                        }
                        ct1DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR,i);
                                        calendar.set(Calendar.MONTH,i1);
                                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                                        ct1DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                                        c1D=docDateFormat.format(calendar.getTime());
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                            }
                        });

                        ct2MarkAddRsltRecordET.setText(pCT2Mark);
                        if (!pCT2Date.equals("Null")){
                            Calendar calendar1=Calendar.getInstance();
                            calendar1.set(Integer.parseInt(pCT2Date.substring(0,4)),Integer.parseInt(pCT2Date.substring(4,6)),Integer.parseInt(pCT2Date.substring(6,8)));
                            ct2DateAddRsltRecordTV.setText(dateFormat.format(calendar1.getTime()));
                        }
                        else {
                            ct2DateAddRsltRecordTV.setText(pCT2Date);
                        }
                        ct2DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR,i);
                                        calendar.set(Calendar.MONTH,i1);
                                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                                        ct2DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                                        c2D=docDateFormat.format(calendar.getTime());
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                            }
                        });

                        ct3MarkAddRsltRecordET.setText(pCT3Mark);
                        if (!pCT3Date.equals("Null")){
                            Calendar calendar1=Calendar.getInstance();
                            calendar1.set(Integer.parseInt(pCT3Date.substring(0,4)),Integer.parseInt(pCT3Date.substring(4,6)),Integer.parseInt(pCT3Date.substring(6,8)));
                            ct3DateAddRsltRecordTV.setText(dateFormat.format(calendar1.getTime()));
                        }
                        else {
                            ct3DateAddRsltRecordTV.setText(pCT3Date);
                        }
                        ct3DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR,i);
                                        calendar.set(Calendar.MONTH,i1);
                                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                                        ct3DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                                        c3D=docDateFormat.format(calendar.getTime());
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                            }
                        });

                        assigmntMarkAddRsltRecordET.setText(pAssigmntMark);
                        if (!pAssigmntDate.equals("Null")){
                            Calendar calendar1=Calendar.getInstance();
                            calendar1.set(Integer.parseInt(pAssigmntDate.substring(0,4)),Integer.parseInt(pAssigmntDate.substring(4,6)),Integer.parseInt(pAssigmntDate.substring(6,8)));
                            assigmntDateAddRsltRecordTV.setText(dateFormat.format(calendar1.getTime()));
                        }
                        else {
                            assigmntDateAddRsltRecordTV.setText(pAssigmntDate);
                        }
                        assigmntDateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR,i);
                                        calendar.set(Calendar.MONTH,i1);
                                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                                        assigmntDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                                        aD=docDateFormat.format(calendar.getTime());
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                            }
                        });

                        finalMarkAddRsltRecordET.setText(pFinalMark);
                        if (!pFinalDate.equals("Null")){
                            Calendar calendar1=Calendar.getInstance();
                            calendar1.set(Integer.parseInt(pFinalDate.substring(0,4)),Integer.parseInt(pFinalDate.substring(4,6)),Integer.parseInt(pFinalDate.substring(6,8)));
                            finalDateAddRsltRecordTV.setText(dateFormat.format(calendar1.getTime()));
                        }
                        else {
                            finalDateAddRsltRecordTV.setText(pFinalDate);
                        }
                        finalDateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR,i);
                                        calendar.set(Calendar.MONTH,i1);
                                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                                        finalDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                                        fD=docDateFormat.format(calendar.getTime());
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(ResultRecord.this, "Don't exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ResultRecord.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveAddRsltRecordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ct1Mark=ct1MarkAddRsltRecordET.getText().toString();
                if (ct1Mark.isEmpty()){
                    ct1MarkAddRsltRecordET.setError("Enter Mark");
                    return;
                }
                String ct1Date;
                if (c1D!=null){
                    ct1Date=c1D;
                }
                else {
                    ct1Date=pCT1Date;
                }

                String ct2Mark=ct2MarkAddRsltRecordET.getText().toString();
                String ct2Date;
                if (c2D!=null){
                    ct2Date=c2D;
                }
                else {
                    ct2Date=pCT2Date;
                }

                String ct3Mark=ct3MarkAddRsltRecordET.getText().toString();
                String ct3Date;
                if (c3D!=null){
                    ct3Date=c3D;
                }
                else {
                    ct3Date=pCT3Date;
                }
                String assigmntMark=assigmntMarkAddRsltRecordET.getText().toString();
                String assigmntDate;
                if (aD != null){
                    assigmntDate=aD;
                }
                else {
                    assigmntDate=pAssigmntDate;
                }
                String finalMark=finalMarkAddRsltRecordET.getText().toString();
                String finalDate;
                if (fD != null){
                    finalDate=fD;
                }
                else {
                    finalDate=pFinalDate;
                }

                String courseCode=courseCodeAddRsltRecordS.getSelectedItem().toString();
                DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                        .collection("Results").document(courseCode);
                Map<String,Object> result=new HashMap<>();
                result.put("CourseCode",courseCode);
                result.put("CT1Mark",ct1Mark);
                result.put("CT1Date",ct1Date);
                result.put("CT2Mark",ct2Mark);
                result.put("CT2Date",ct2Date);
                result.put("CT3Mark",ct3Mark);
                result.put("CT3Date",ct3Date);
                result.put("AssignmentMark",assigmntMark);
                result.put("AssignmentDate",assigmntDate);
                result.put("FinalMark",finalMark);
                result.put("FinalDate",finalDate);
                documentReference.set(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResultRecord.this, "Result Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResultRecord.this, "Result Failed", Toast.LENGTH_SHORT).show();
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

    private void addResultRecordDialog() {
        Dialog dialog=new Dialog(ResultRecord.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_result_record);

        Calendar calendar=Calendar.getInstance();

        Spinner courseCodeAddRsltRecordS=dialog.findViewById(R.id.courseCodeAddRsltRecordS);
        EditText ct1MarkAddRsltRecordET=dialog.findViewById(R.id.ct1MarkAddRsltRecordET);
        TextView ct1DateAddRsltRecordTV=dialog.findViewById(R.id.ct1DateAddRsltRecordTV);
        EditText ct2MarkAddRsltRecordET=dialog.findViewById(R.id.ct2MarkAddRsltRecordET);
        TextView ct2DateAddRsltRecordTV=dialog.findViewById(R.id.ct2DateAddRsltRecordTV);
        EditText ct3MarkAddRsltRecordET=dialog.findViewById(R.id.ct3MarkAddRsltRecordET);
        TextView ct3DateAddRsltRecordTV=dialog.findViewById(R.id.ct3DateAddRsltRecordTV);
        EditText assigmntMarkAddRsltRecordET=dialog.findViewById(R.id.assigmntMarkAddRsltRecordET);
        TextView assigmntDateAddRsltRecordTV=dialog.findViewById(R.id.assigmntDateAddRsltRecordTV);
        EditText finalMarkAddRsltRecordET=dialog.findViewById(R.id.finalMarkAddRsltRecordET);
        TextView finalDateAddRsltRecordTV=dialog.findViewById(R.id.finalDateAddRsltRecordTV);
        Button saveAddRsltRecordB=dialog.findViewById(R.id.saveAddRsltRecordB);

        List<String> courses=new ArrayList<>();
        ArrayAdapter<String> courseAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseCodeAddRsltRecordS.setAdapter(courseAdapter);

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
                    Toast.makeText(ResultRecord.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        courseCodeAddRsltRecordS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ct1DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));

        ct1DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        ct1DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                        c1D=docDateFormat.format(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        ct2DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));

        ct2DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        ct2DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                        c2D=docDateFormat.format(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        ct3DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));

        ct3DateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        ct3DateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                        c3D=docDateFormat.format(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        assigmntDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));

        assigmntDateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        assigmntDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                        aD=docDateFormat.format(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        finalDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));

        finalDateAddRsltRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResultRecord.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        finalDateAddRsltRecordTV.setText(dateFormat.format(calendar.getTime()));
                        fD=docDateFormat.format(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
            }
        });

        saveAddRsltRecordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ct1Mark=ct1MarkAddRsltRecordET.getText().toString();
                if (ct1Mark.isEmpty()){
                    ct1MarkAddRsltRecordET.setError("Enter Mark");
                    return;
                }

                String ct2Mark=ct2MarkAddRsltRecordET.getText().toString();
                String ct2Date;
                if (ct2Mark.isEmpty()){
                    ct2Mark="Null";
                    ct2Date="Null";
                }
                else {
                    ct2Date=c2D;
                }

                String ct3Mark=ct3MarkAddRsltRecordET.getText().toString();
                String ct3Date;
                if (ct3Mark.isEmpty()){
                    ct3Mark="Null";
                    ct3Date="Null";
                }
                else {
                    ct3Date=c3D;
                }
                String assigmntMark=assigmntMarkAddRsltRecordET.getText().toString();
                String assigmntDate;
                if (assigmntMark.isEmpty()){
                    assigmntMark="Null";
                    assigmntDate="Null";
                }
                else {
                    assigmntDate=aD;
                }

                String finalMark=finalMarkAddRsltRecordET.getText().toString();
                String finalDate;
                if (finalMark.isEmpty()){
                    finalMark="Null";
                    finalDate="Null";
                }
                else {
                    finalDate=fD;
                }

                String courseCode=courseCodeAddRsltRecordS.getSelectedItem().toString();
                DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                        .collection("Results").document(courseCode);
                Map<String,Object> result=new HashMap<>();
                result.put("CourseCode",courseCode);
                result.put("CT1Mark",ct1Mark);
                result.put("CT1Date",c1D);
                result.put("CT2Mark",ct2Mark);
                result.put("CT2Date",ct2Date);
                result.put("CT3Mark",ct3Mark);
                result.put("CT3Date",ct3Date);
                result.put("AssignmentMark",assigmntMark);
                result.put("AssignmentDate",assigmntDate);
                result.put("FinalMark",finalMark);
                result.put("FinalDate",finalDate);
                documentReference.set(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResultRecord.this, "Result Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResultRecord.this, "Result Failed", Toast.LENGTH_SHORT).show();
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

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView resultCodeItemTV, resultCT1MarkItemTV, resultCT2MarkItemTV, resultCT3MarkItemTV,
                resultCT1DateItemTV, resultCT2DateItemTV, resultCT3DateItemTV, resultAssigmntMarkItemTV,
                resultAssigmntDateItemTV, resultFinalMarkItemTV, resultFinalDateItemTV;
        View view;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            resultCodeItemTV=itemView.findViewById(R.id.resultCodeItemTV);
            resultCT1MarkItemTV=itemView.findViewById(R.id.resultCT1MarkItemTV);
            resultCT1DateItemTV=itemView.findViewById(R.id.resultCT1DateItemTV);
            resultCT2MarkItemTV=itemView.findViewById(R.id.resultCT2MarkItemTV);
            resultCT2DateItemTV=itemView.findViewById(R.id.resultCT2DateItemTV);
            resultCT3MarkItemTV=itemView.findViewById(R.id.resultCT3MarkItemTV);
            resultCT3DateItemTV=itemView.findViewById(R.id.resultCT3DateItemTV);
            resultAssigmntMarkItemTV=itemView.findViewById(R.id.resultAssigmntMarkItemTV);
            resultAssigmntDateItemTV=itemView.findViewById(R.id.resultAssigmntDateItemTV);
            resultFinalMarkItemTV=itemView.findViewById(R.id.resultFinalMarkItemTV);
            resultFinalDateItemTV=itemView.findViewById(R.id.resultFinalDateItemTV);
            view=itemView;
        }
    }
}