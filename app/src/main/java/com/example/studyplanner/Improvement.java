package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Improvement extends Base {
    LayoutInflater inflater;
    XYPlot plot;
    SimpleDateFormat docDateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_improvement,null,false);
        drawerLayout.addView(contentView,0);

        plot = findViewById(R.id.plot);

        getSupportActionBar().setTitle("Study Track");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24,null));
        }

        mainSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImprovementDialog();
            }
        });

        DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                .collection("Data").document("Improvement");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        Map<String, Object> map = documentSnapshot.getData();
                        for (Map.Entry<String, Object> entry:map.entrySet()){
                            if (entry.getKey().equals("Update")){
                                Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                String[] domainLabels={};
                                Number[] series1Numbers ={};

                                for (Map.Entry<String,Object> dataEntry:newFriend0Map.entrySet()){
                                    domainLabels=addStringElement(domainLabels,dataEntry.getKey());
                                    try {
                                        series1Numbers=addNumberElement(series1Numbers, NumberFormat.getInstance().parse(dataEntry.getValue().toString()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(Improvement.this, "key"+dataEntry.getKey().toString(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Improvement.this, "value"+dataEntry.getValue().toString(), Toast.LENGTH_SHORT).show();
                                }
                                if (domainLabels.length<10){
                                    for (int i=domainLabels.length;i<10;i++){
                                        domainLabels=addStringElement(domainLabels,"Null");
                                        series1Numbers=addNumberElement(series1Numbers,0);
                                    }
                                }
                                plot.clear();
                                drawPlot(domainLabels,series1Numbers);
                                plot.redraw();
                            }
                        }
                    }
                    else {
                        Toast.makeText(Improvement.this, "No such one", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Improvement.this, "failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13};
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16};

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    private void drawPlot(String[] domainLabels,Number[] series1Numbers) {
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Improvement.this, R.xml.line_point_formatter_with_labels);
        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    private void updateImprovementDialog() {
        Dialog dialog=new Dialog(Improvement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_update_improvement);

        EditText updateImpvmntET=dialog.findViewById(R.id.updateImpvmntET);
        Button saveImpvmntB=dialog.findViewById(R.id.saveImpvmntB);

        saveImpvmntB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String update=updateImpvmntET.getText().toString();
                DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid())
                        .collection("Data").document("Improvement");
                Map<String,Object> docData=new HashMap<>();
                Map<String,Object> nestedData=new HashMap<>();
                nestedData.put(docDateFormat.format(calendar.getTime()),update);
                docData.put("Update",nestedData);
                documentReference.set(docData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Improvement.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Improvement.this, "Error", Toast.LENGTH_SHORT).show();
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

    static String[] addStringElement(String[] a, String e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    static Number[] addNumberElement(Number[] a, Number e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
}