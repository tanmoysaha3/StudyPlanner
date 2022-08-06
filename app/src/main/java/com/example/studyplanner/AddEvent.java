package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.preference.PowerPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    private static final String TAG = "Log - AddEventActivity";

    Calendar calendar=Calendar.getInstance();
    SimpleDateFormat dateFormat=new SimpleDateFormat( "EEE, dd MMM yyyy", Locale.getDefault());
    SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm",Locale.getDefault());
    SimpleDateFormat docIdFormat=new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
    SimpleDateFormat docDateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
    SimpleDateFormat docTimeFormat=new SimpleDateFormat("HH-mm",Locale.getDefault());

    Calendar startCalendar= Calendar.getInstance();
    Calendar endCalendar=Calendar.getInstance();

    ImageButton closeAddEventIB;
    Button saveAddEventB;
    TextView eventDateTV, eventStartTimeTV, eventEndTimeTV, eventRepeatTV, eventNotifTV;
    Switch publicSw;
    RadioButton repeatRB, notifRB;
    EditText addNoteET;
    Spinner eventTypeS,courseS;

    int repeatCheckedId=0;
    int notifCheckedId=0;
    String repeatValue="NoRepeat";
    int intentDuration;
    List<String> eventTypes;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    String repeatInterval="0", notificationInterval=String.valueOf(5*60*1000);
    long notificationSubTime;

    String currentYearPref, currentSemesterPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Intent data=getIntent();
        String intentDate=data.getStringExtra("Date");
        String intentEventType=data.getStringExtra("EventType");
        intentDuration=data.getIntExtra("Duration",0);
        String[] dateParts=intentDate.split("/");
        int selectedDay=Integer.parseInt(dateParts[0]);
        int selectedMonth=Integer.parseInt(dateParts[1]);
        int selectedYear=Integer.parseInt(dateParts[2]);

        closeAddEventIB=findViewById(R.id.closeAddEventIB);
        saveAddEventB=findViewById(R.id.saveAddEventB);
        courseS=findViewById(R.id.courseS);
        eventTypeS=findViewById(R.id.eventTypeS);
        addNoteET=findViewById(R.id.addNoteET);
        eventDateTV=findViewById(R.id.eventDateTV);
        eventStartTimeTV=findViewById(R.id.eventStartTimeTV);
        eventEndTimeTV=findViewById(R.id.eventEndTimeTV);
        eventRepeatTV=findViewById(R.id.eventRepeatTV);
        publicSw=findViewById(R.id.publicSw);
        eventNotifTV=findViewById(R.id.eventNotifTV);

        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();

        startCalendar.set(selectedYear,selectedMonth-1,selectedDay);
        endCalendar.set(selectedYear,selectedMonth-1,selectedDay);
        endCalendar.add(Calendar.MINUTE,intentDuration);
        eventDateTV.setText(dateFormat.format(startCalendar.getTime()));
        eventStartTimeTV.setText(timeFormat.format(startCalendar.getTime()));
        eventEndTimeTV.setText(timeFormat.format(endCalendar.getTime()));

        Toast.makeText(this, intentDate+" "+intentEventType, Toast.LENGTH_SHORT).show();
        if (intentEventType.equals("Class")){
            eventTypes= Arrays.asList(getResources().getStringArray(R.array.classes));
        }
        else if (intentEventType.equals("Lab")){
            eventTypes=Arrays.asList(getResources().getStringArray(R.array.lab));
        }
        else if (intentEventType.equals("CT")){
            eventTypes=Arrays.asList(getResources().getStringArray(R.array.cT));
        }
        else if (intentEventType.equals("Assignment")){
            eventTypes=Arrays.asList(getResources().getStringArray(R.array.asgmtReport));
        }
        else if (intentEventType.equals("Exam")){
            eventTypes=Arrays.asList(getResources().getStringArray(R.array.examLFinal));
        }

        Toast.makeText(this, intentDate+" "+intentEventType, Toast.LENGTH_SHORT).show();

        currentYearPref= PowerPreference.getDefaultFile().getString("CurrentYear");
        currentSemesterPref=PowerPreference.getDefaultFile().getString("CurrentSemester");

        ArrayAdapter<String> eventTypeAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeS.setAdapter(eventTypeAdapter);
        eventTypeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> courses=new ArrayList<>();
                ArrayAdapter<String> courseAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,courses);
                courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                courseS.setAdapter(courseAdapter);

                String selectedEventType=eventTypeS.getSelectedItem().toString();
                if (selectedEventType.equals("Class")||selectedEventType.equals("Class Test") || selectedEventType.equals("Assignment") || selectedEventType.equals("Semester Final")){
                    getCourseList(courses,courseAdapter,"Written");
                }
                else if (selectedEventType.equals("Lab") || selectedEventType.equals("Lab Test") || selectedEventType.equals("Lab Final") || selectedEventType.equals("Lab Report")){
                    getCourseList(courses,courseAdapter,"Lab");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEvent.this,datePickerF(),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        eventStartTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEvent.this,startTimePickerF(),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
            }
        });

        eventEndTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEvent.this,endTimePickerF(),endCalendar.get(Calendar.HOUR_OF_DAY),endCalendar.get(Calendar.MINUTE),true).show();
            }
        });

        publicSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(AddEvent.this, " "+isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        eventRepeatTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatFirstDialog();
            }
        });

        eventNotifTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationTimeDialog();
            }
        });

        saveAddEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startCalendar.getTimeInMillis()<(Calendar.getInstance().getTimeInMillis()-(60*1000))){
                    Toast.makeText(AddEvent.this, docIdFormat.format(startCalendar), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AddEvent.this, docIdFormat.format(Calendar.getInstance().getTimeInMillis()-(60*1000)), Toast.LENGTH_SHORT).show();
                    eventDateTV.setError("Can't Save Past Event For now");
                    eventStartTimeTV.setError("Can't Save Past Event For now");
                    return;
                }
                String type=eventTypeS.getSelectedItem().toString();
                String courseName=courseS.getSelectedItem().toString();
                String note=addNoteET.getText().toString();
                boolean publicState=publicSw.isChecked();

                if (repeatValue=="NoRepeat"){
                    notificationSubTime=startCalendar.getTimeInMillis()-Long.parseLong(notificationInterval);
                    long currentEvent=Long.parseLong(PowerPreference.getDefaultFile().getString("CurrentEvent","0"));
                    if (currentEvent==0 || currentEvent>Long.parseLong(docIdFormat.format(notificationSubTime)) || currentEvent<Long.parseLong(docIdFormat.format(Calendar.getInstance().getTime()))){
                        PowerPreference.getDefaultFile().putString("CurrentEvent",docIdFormat.format(notificationSubTime));
                        Toast.makeText(AddEvent.this, "id==="+docIdFormat.format(notificationSubTime), Toast.LENGTH_SHORT).show();
                        String notId=docIdFormat.format(notificationSubTime);
                        scheduleNotification(notificationSubTime,Integer.parseInt(notId.substring(notId.length()-6)),
                                courseName+type, timeFormat.format(startCalendar.getTime())+" - "+timeFormat.format(endCalendar.getTime()));
                    }
                    DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid()).collection("Events")
                            .document();
                    Map<String,Object> event=new HashMap<>();
                    event.put("CreationId", docIdFormat.format(startCalendar.getTime()));
                    event.put("NotificationId",docIdFormat.format(notificationSubTime));
                    event.put("EventType",type);
                    event.put("CourseName",courseName);
                    event.put("Note",note);
                    event.put("Date", docDateFormat.format(startCalendar.getTime()));
                    event.put("StartTime", docTimeFormat.format(startCalendar.getTime()));
                    event.put("EndTime",docTimeFormat.format(endCalendar.getTime()));
                    event.put("Repeat",repeatInterval);
                    event.put("PublicState",publicState);
                    event.put("Notification",notificationInterval);
                    documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddEvent.this, "Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ReminderList.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEvent.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (repeatValue=="Daily"){
                    for(int i=0; i<10;i++){
                        notificationSubTime=startCalendar.getTimeInMillis()-Long.parseLong(notificationInterval);
                        long currentEvent=Long.parseLong(PowerPreference.getDefaultFile().getString("CurrentEvent","0"));
                        if (currentEvent==0 || currentEvent>Long.parseLong(docIdFormat.format(notificationSubTime)) || currentEvent<Long.parseLong(docIdFormat.format(Calendar.getInstance().getTime()))){
                            PowerPreference.getDefaultFile().putString("CurrentEvent",docIdFormat.format(notificationSubTime));
                            Toast.makeText(AddEvent.this, "id==="+docIdFormat.format(notificationSubTime), Toast.LENGTH_SHORT).show();
                            String notId=docIdFormat.format(notificationSubTime);
                            scheduleNotification(notificationSubTime,Integer.parseInt(notId.substring(notId.length()-6)),
                                    courseName+type, timeFormat.format(startCalendar.getTime())+" - "+timeFormat.format(endCalendar.getTime()));
                        }
                        DocumentReference documentReference=fStore.collection("NonShared").document(fUser.getUid()).collection("Events")
                                .document();
                        Map<String,Object> event=new HashMap<>();
                        event.put("CreationId", docIdFormat.format(startCalendar.getTime()));
                        event.put("NotificationId",docIdFormat.format(notificationSubTime));
                        event.put("EventType",type);
                        event.put("CourseName",courseName);
                        event.put("Note",note);
                        event.put("Date", docDateFormat.format(startCalendar.getTime()));
                        event.put("StartTime", docTimeFormat.format(startCalendar.getTime()));
                        event.put("EndTime",docTimeFormat.format(endCalendar.getTime()));
                        event.put("Repeat",repeatInterval);
                        event.put("PublicState",publicState);
                        event.put("Notification",notificationInterval);
                        documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddEvent.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddEvent.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startCalendar.add(Calendar.DAY_OF_MONTH,1);
                        endCalendar.add(Calendar.DAY_OF_MONTH,1);
                    }
                }
            }
        });

        closeAddEventIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AddEvent.this,R.style.MyThemeOverlayAlertDialog)
                        .setTitle("Discard this?")
                        .setPositiveButton("Keep editing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(),ReminderList.class));
                            }
                        })
                        .show();
            }
        });
    }

    private void getCourseList(List<String> courses, ArrayAdapter courseAdapter, String courseType){
        Query query=fStore.collection("NonShared").document(fAuth.getUid()).collection("CourseList")
                .document(currentYearPref).collection(currentSemesterPref).whereEqualTo("Type",courseType);

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
                    Toast.makeText(AddEvent.this, "Failed with"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        courseS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private DatePickerDialog.OnDateSetListener datePickerF(){
        return  new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startCalendar.set(Calendar.YEAR,year);
                startCalendar.set(Calendar.MONTH,month);
                startCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDate(startCalendar);
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener startTimePickerF(){
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                startCalendar.set(Calendar.MINUTE,minute);
                startCalendar.set(Calendar.SECOND,Calendar.getInstance().get(Calendar.SECOND));
                updateStartTime(startCalendar);

                endCalendar.setTimeInMillis(startCalendar.getTimeInMillis()+intentDuration*60000);
                updateEndTime(endCalendar);
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener endTimePickerF(){
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                endCalendar.set(Calendar.MINUTE,minute);
                updateEndTime(endCalendar);
            }
        };
    }

    private void updateDate(Calendar calendar){
        eventDateTV.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateStartTime(Calendar calendar){
        String time=timeFormat.format(calendar.getTime());
        String[] timeParts=time.split(":");
        String hour=timeParts[0];
        if (Integer.parseInt(timeParts[0])==24){
            hour="00";
        }
        eventStartTimeTV.setText(hour+":"+timeParts[1]);
    }

    private void updateEndTime(Calendar calendar){
        String time=timeFormat.format(calendar.getTime());
        String[] timeParts=time.split(":");
        String hour=timeParts[0];
        if (Integer.parseInt(timeParts[0])==24){
            hour="00";
        }
        eventEndTimeTV.setText(hour+":"+timeParts[1]);
    }

    private void repeatFirstDialog() {
        androidx.appcompat.app.AlertDialog materialDialogs=new MaterialAlertDialogBuilder(AddEvent.this, R.style.MyThemeOverlayAlertDialog).create();
        materialDialogs.setCancelable(true);
        View customAlertDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_repeat_first, null, false);
        materialDialogs.setView(customAlertDialogView);
        RadioGroup repeatFirstRG=customAlertDialogView.findViewById(R.id.reapeatFirstRG);
        if (repeatCheckedId==0){
            repeatFirstRG.check(repeatFirstRG.getChildAt(0).getId());
        }
        else {
            repeatFirstRG.check(repeatCheckedId);
        }
        repeatFirstRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                repeatRB = customAlertDialogView.findViewById(checkedId);
                repeatCheckedId=repeatRB.getId();
                eventRepeatTV.setText(repeatRB.getText());
                switch(checkedId){
                    case R.id.dayRepeatRB:
                        repeatValue="Daily";
                        break;
                    case R.id.weekRepeatRB:
                        repeatValue="Weekly";
                        break;
                    case R.id.monthRepeatRB:
                        repeatValue="Monthly";
                        break;
                    case R.id.yearRepeatRB:
                        repeatValue="Yearly";
                        break;
                    case R.id.noRepeatRB:
                    default:
                        repeatValue="NoRepeat";
                        break;
                }
                materialDialogs.dismiss();
            }
        });
        materialDialogs.show();
    }

    private void notificationTimeDialog(){
        androidx.appcompat.app.AlertDialog notifTimeDialog=new MaterialAlertDialogBuilder(AddEvent.this, R.style.MyThemeOverlayAlertDialog).create();
        notifTimeDialog.setCancelable(true);
        View customAlertDialogView=LayoutInflater.from(this).inflate(R.layout.dialog_notification_time,null,false);
        notifTimeDialog.setView(customAlertDialogView);
        RadioGroup notifTimeRG=customAlertDialogView.findViewById(R.id.notifTimeRG);
        if (notifCheckedId==0){
            notifTimeRG.check(notifTimeRG.getChildAt(0).getId());
        }
        else {
            notifTimeRG.check(notifCheckedId);
        }
        notifTimeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                notifRB=customAlertDialogView.findViewById(checkedId);
                notifCheckedId=notifRB.getId();
                eventNotifTV.setText(notifRB.getText());
                switch (checkedId){
                    case R.id.tenMinRB:
                        notificationInterval=String.valueOf(10*60*1000);
                        break;
                    case R.id.fifMinRB:
                        notificationInterval=String.valueOf(15*60*1000);
                        break;
                    case R.id.thirMinRB:
                        notificationInterval=String.valueOf(30*60*1000);
                        break;
                    case R.id.hourMinRB:
                        notificationInterval=String.valueOf(60*60*1000);
                        break;
                    case R.id.twelveHourMinRB:
                        notificationInterval=String.valueOf(12*60*60*1000);
                        break;
                    case R.id.dayMinRB:
                        notificationInterval=String.valueOf(24*60*60*1000);
                        break;
                    case R.id.fiveMinRB:
                    default:
                        notificationInterval=String.valueOf(5*60*1000);
                        break;
                }
                notifTimeDialog.dismiss();
            }
        });
        notifTimeDialog.show();
    }

    private void scheduleNotification(long time,int notificationId, String title, String message) {
        Intent intent=new Intent(getApplicationContext(),NotificationReceiver.class);
        intent.putExtra("titleExtra",title);
        intent.putExtra("messageExtra",message);

        calendar.setTimeInMillis(time);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),notificationId,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
        );
    }
}