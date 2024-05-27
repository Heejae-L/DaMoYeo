package android.org.firebasetest;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private AlarmManager alarmManager;
    private NotificationHelper mNotificationHelper;
    private TextView mTextView;
    Button buttonCancelAlarm;
    private String userId;
    private DatabaseReference database;

    private static final String CHANNEL_ID = "channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        database = FirebaseDatabase.getInstance().getReference();
        userId = getIntent().getStringExtra("userId");

        timePicker = findViewById(R.id.timePicker);
        mTextView =  findViewById(R.id.textView);
        buttonCancelAlarm = findViewById(R.id.btnCancel);
        loadAlarmTime();

        findViewById(R.id.btnSetAlarm).setOnClickListener(v -> setAlarm());
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                cancelAlarm();
            }
        });
    }

    private void setAlarm() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Save alarm time to Firebase
        database.child("alarms").child(userId).setValue(calendar.getTimeInMillis());

        updateTimeText(calendar);
        startAlarm(calendar);
    }
    private void loadAlarmTime() {
        database.child("alarms").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long time = dataSnapshot.getValue(Long.class);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    updateTimeText(calendar);
                    timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("SetAlarmActivity", "loadAlarmTime:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateTimeText(Calendar c){
        String timeText = "Alarm set for : ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        mTextView.setText(timeText);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", "다모여");
        intent.putExtra("message", "하루의 일을 다이어리로 작성해보세요!");
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);


        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
    }



    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        // Add FLAG_IMMUTABLE to the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        mTextView.setText("Alarm canceled");
    }


}
