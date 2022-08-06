package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.FabOption;
import com.preference.PowerPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderList extends Base {
    LayoutInflater inflater;

    SimpleDateFormat docDateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    CalendarView calendarView;
    RecyclerView remindListRecV;
    Calendar selectedDay=Calendar.getInstance();

    String date;
    ExpandableFab expandableFab;
    FabOption classFABO, labFABO, cTFABO, asgmtReportFABO, examFBO;
    FirestorePagingAdapter<ReminderModel, RemindViewHolder> reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView=inflater.inflate(R.layout.activity_reminder_list,null,false);
        drawerLayout.addView(contentView,0);

        getSupportActionBar().setTitle("Calendar");

        calendarView=findViewById(R.id.calendarView);
        remindListRecV=findViewById(R.id.remindListRecV);
        expandableFab=findViewById(R.id.expandable_fab);
        classFABO=findViewById(R.id.classFABO);
        labFABO=findViewById(R.id.labFABO);
        cTFABO=findViewById(R.id.cTFABO);
        asgmtReportFABO=findViewById(R.id.asgmtReportFABO);
        examFBO=findViewById(R.id.examFABO);

        calendarView.setFirstDayOfWeek(4);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth+"/"+(month)+"/"+year;
                selectedDay.set(year,month,dayOfMonth);
                Query query=fStore.collection("NonShared").document(fUser.getUid()).collection("Events")
                        .whereEqualTo("Date",docDateFormat.format(selectedDay.getTime())).orderBy("CreationId");
                Toast.makeText(ReminderList.this, "REFRESH"+docDateFormat.format(selectedDay.getTime()), Toast.LENGTH_SHORT).show();
                recV(query);
            }
        });

        Query query=fStore.collection("NonShared").document(fUser.getUid()).collection("Events")
                .whereEqualTo("Date",docDateFormat.format(selectedDay.getTime())).orderBy("CreationId");
        Toast.makeText(ReminderList.this, "REFRESH"+docDateFormat.format(selectedDay.getTime()), Toast.LENGTH_SHORT).show();
        recV(query);

        classFABO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Class",45);
            }
        });

        labFABO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Lab",180);
            }
        });

        cTFABO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("CT",45);
            }
        });

        asgmtReportFABO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Assignment",0);
            }
        });

        examFBO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Exam",180);
            }
        });
    }

    private void recV(Query query) {
        PagingConfig config=new PagingConfig(
                20,10,false);

        FirestorePagingOptions<ReminderModel> options = new FirestorePagingOptions.Builder<ReminderModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ReminderModel.class)
                .build();

        reminderAdapter=new FirestorePagingAdapter<ReminderModel, RemindViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RemindViewHolder holder, int position, @NonNull ReminderModel model) {
                holder.remindTitleItemTV.setText(model.getCourseName()+" "+model.getEventType());
                holder.remindTimeItemTV.setText(model.getStartTime()+" "+model.getEndTime());
            }

            @NonNull
            @Override
            public RemindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reminder,parent,false);
                return new RemindViewHolder(view);
            }
        };

        remindListRecV.setHasFixedSize(true);
        remindListRecV.setLayoutManager(new LinearLayoutManager(this));
        remindListRecV.setAdapter(reminderAdapter);
    }

    private void sendIntent(String eventType, int duration){
        Toast.makeText(ReminderList.this, "Class clicked", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),AddEvent.class);
        if (date==null){
            Date c= Calendar.getInstance().getTime();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
            date=sdf1.format(c);
        }
        intent.putExtra("Date",date);
        intent.putExtra("EventType",eventType);
        intent.putExtra("Duration",duration);
        startActivity(intent);
    }

    public class RemindViewHolder extends RecyclerView.ViewHolder {
        TextView remindTitleItemTV, remindTimeItemTV;
        View view;
        public RemindViewHolder(@NonNull View itemView) {
            super(itemView);
            remindTitleItemTV=itemView.findViewById(R.id.remindTitleItemTV);
            remindTimeItemTV=itemView.findViewById(R.id.remindTimeItemTV);
            view=itemView;
        }
    }
}