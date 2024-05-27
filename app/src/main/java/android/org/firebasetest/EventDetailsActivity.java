package android.org.firebasetest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class EventDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewDetails;
    private EventDetailsAdapter adapter;
    private ArrayList<EventDetail> eventDetailsList;
    private EventDetail selectedEventDetail;
    private com.google.api.services.calendar.Calendar mService;
    private GoogleAccountCredential mCredential;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        TextView tvEventDate = findViewById(R.id.tvEventDate);
        recyclerViewDetails = findViewById(R.id.recyclerViewDetails);
        recyclerViewDetails.setLayoutManager(new LinearLayoutManager(this));

        // 버튼 찾기
        Button backToMainButton = findViewById(R.id.backToMainButton);
        Button deleteButton = findViewById(R.id.Deletebtn);
        Button rewriteButton = findViewById(R.id.rewritebtn);

        // 인텐트에서 이벤트 정보를 가져옵니다.
        Intent intent = getIntent();
        int year = intent.getIntExtra("YEAR", 0);
        int month = intent.getIntExtra("MONTH", 0) + 1; // month 값 조정 (0-11 to 1-12)
        int day = intent.getIntExtra("DAY", 0);
        String accountName = intent.getStringExtra("ACCOUNT_NAME"); // 계정 정보 가져오기
        ArrayList<EventDetail> eventDetails = intent.getParcelableArrayListExtra("eventDetails");

        // Google Calendar API 서비스 초기화
        mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(accountName); // 전달받은 계정 설정

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Test Application")
                .build();

        // 날짜 정보가 있을 경우 TextView에 표시
        if (year != 0 && month != 0 && day != 0) {
            String date = String.format("%d년 %d월 %d일", year, month-1, day);
            tvEventDate.setText(date);
        } else {
            tvEventDate.setText("날짜 정보 없음");
        }


        // 이벤트 상세 정보 설정
        if (eventDetails != null) {
            eventDetailsList = eventDetails;
            adapter = new EventDetailsAdapter(eventDetailsList, new EventDetailsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(EventDetail eventDetail) {
                    selectedEventDetail = eventDetail;
                }
            });
            recyclerViewDetails.setAdapter(adapter);
        } else {
            eventDetailsList = new ArrayList<>();
            adapter = new EventDetailsAdapter(eventDetailsList, null);
            recyclerViewDetails.setAdapter(adapter);
            eventDetailsList.add(new EventDetail("이벤트 정보가 없습니다.", new DateTime(System.currentTimeMillis()), null));
            adapter.notifyDataSetChanged();
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedEventDetail != null) {
                    new DeleteEventTask().execute(selectedEventDetail);
                } else {
                    Toast.makeText(EventDetailsActivity.this, "삭제할 이벤트를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rewriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedEventDetail != null) {
                    Intent intent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
                    intent.putExtra("ACCOUNT_NAME", mCredential.getSelectedAccountName());
                    intent.putExtra("IS_EDITING", true);
                    intent.putExtra("EVENT_ID", selectedEventDetail.getEventId());
                    intent.putExtra("TITLE", selectedEventDetail.getSummary());
                    intent.putExtra("LOCATION", selectedEventDetail.getLocation());
                    intent.putExtra("DESCRIPTION", selectedEventDetail.getDescription());
                    intent.putExtra("START_DATE", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selectedEventDetail.getStartTime().getValue())));
                    intent.putExtra("START_TIME", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(selectedEventDetail.getStartTime().getValue())));
                    if (selectedEventDetail.getEndTime() != null) {
                        intent.putExtra("END_DATE", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selectedEventDetail.getEndTime().getValue())));
                        intent.putExtra("END_TIME", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(selectedEventDetail.getEndTime().getValue())));
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(EventDetailsActivity.this, "수정할 이벤트를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private class UpdateEventTask extends AsyncTask<Void, Void, Boolean> {
        private String eventId;
        private String title;
        private String location;
        private String description;
        private Date startDateTime;
        private Date endDateTime;

        public UpdateEventTask(String eventId, String title, String location, String description, Date startDateTime, Date endDateTime) {
            this.eventId = eventId;
            this.title = title;
            this.location = location;
            this.description = description;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String calendarID = getCalendarID("CalendarTitle");
                if (calendarID != null) {
                    Event event = mService.events().get(calendarID, eventId).execute();
                    event.setSummary(title);
                    event.setLocation(location);
                    event.setDescription(description);

                    DateTime start = new DateTime(startDateTime);
                    DateTime end = new DateTime(endDateTime);
                    event.setStart(new EventDateTime().setDateTime(start).setTimeZone("Asia/Seoul"));
                    event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("Asia/Seoul"));

                    mService.events().update(calendarID, event.getId(), event).execute();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EventDetailsActivity.this, "이벤트가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                int index = eventDetailsList.indexOf(selectedEventDetail);
                selectedEventDetail.setSummary(title); // setTitle 대신 setSummary 사용
                selectedEventDetail.setLocation(location);
                selectedEventDetail.setDescription(description);
                selectedEventDetail.setStartTime(new DateTime(startDateTime));
                selectedEventDetail.setEndTime(new DateTime(endDateTime));
                adapter.notifyItemChanged(index);
            } else {
                Toast.makeText(EventDetailsActivity.this, "이벤트 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCustomTimePicker(EditText timeInput) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_time_picker);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);
        NumberPicker hourPicker = dialog.findViewById(R.id.hour_picker);
        NumberPicker minutePicker = dialog.findViewById(R.id.minute_picker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        confirmButton.setOnClickListener(v -> {
            int selectedHour = hourPicker.getValue();
            int selectedMinute = minutePicker.getValue();
            timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            dialog.dismiss();
        });

        dialog.show();
    }


    private class DeleteEventTask extends AsyncTask<EventDetail, Void, Boolean> {
        @Override
        protected Boolean doInBackground(EventDetail... params) {
            EventDetail eventDetail = params[0];
            try {
                String calendarID = getCalendarID("CalendarTitle");
                if (calendarID != null) {
                    mService.events().delete(calendarID, eventDetail.getEventId()).execute();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EventDetailsActivity.this, "이벤트가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                eventDetailsList.remove(selectedEventDetail);
                adapter.notifyDataSetChanged();
                selectedEventDetail = null;
            } else {
                Toast.makeText(EventDetailsActivity.this, "이벤트 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCalendarID(String calendarTitle) {
        try {
            // 캘린더 ID를 가져오는 로직 구현
            String pageToken = null;
            do {
                CalendarList calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
                List<CalendarListEntry> items = calendarList.getItems();

                for (CalendarListEntry calendarListEntry : items) {
                    if (calendarListEntry.getSummary().equals(calendarTitle)) {
                        return calendarListEntry.getId();
                    }
                }
                pageToken = calendarList.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            Log.e("getCalendarID", "Failed to get calendar ID", e);
        }
        return null;
    }

}
