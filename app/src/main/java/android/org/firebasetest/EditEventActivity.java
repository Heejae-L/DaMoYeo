package android.org.firebasetest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditEventActivity extends AppCompatActivity {
    private GoogleAccountCredential mCredential;
    private com.google.api.services.calendar.Calendar mService;

    private EditText titleInput;
    private EditText locationInput;
    private EditText descriptionInput;
    private EditText startDateInput;
    private EditText startTimeInput;
    private EditText endDateInput;
    private EditText endTimeInput;
    private Button saveEventButton;

    private boolean isEditing;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        titleInput = findViewById(R.id.event_title_input);
        locationInput = findViewById(R.id.event_location_input);
        descriptionInput = findViewById(R.id.event_description_input);
        startDateInput = findViewById(R.id.start_date);
        startTimeInput = findViewById(R.id.start_time);
        endDateInput = findViewById(R.id.end_date);
        endTimeInput = findViewById(R.id.end_time);
        saveEventButton = findViewById(R.id.save_event_button);

        // Credential and Calendar Service Initialization
        mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(getIntent().getStringExtra("ACCOUNT_NAME"));

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Test Application")
                .build();

        Intent intent = getIntent();
        isEditing = intent.getBooleanExtra("IS_EDITING", false);

        if (isEditing) {
            eventId = intent.getStringExtra("EVENT_ID");
            titleInput.setText(intent.getStringExtra("TITLE"));
            locationInput.setText(intent.getStringExtra("LOCATION"));
            descriptionInput.setText(intent.getStringExtra("DESCRIPTION"));

            String startDateStr = intent.getStringExtra("START_DATE");
            String startTimeStr = intent.getStringExtra("START_TIME");
            String endDateStr = intent.getStringExtra("END_DATE");
            String endTimeStr = intent.getStringExtra("END_TIME");

            try {
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                SimpleDateFormat localTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                localTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                Date startDateUtc = utcFormat.parse(startDateStr + "T" + startTimeStr + ":00.000Z");
                Date endDateUtc = utcFormat.parse(endDateStr + "T" + endTimeStr + ":00.000Z");

                startDateInput.setText(localDateFormat.format(startDateUtc));
                startTimeInput.setText(localTimeFormat.format(startDateUtc));
                endDateInput.setText(localDateFormat.format(endDateUtc));
                endTimeInput.setText(localTimeFormat.format(endDateUtc));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        startTimeInput.setOnClickListener(v -> showCustomTimePicker(startTimeInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));
        endTimeInput.setOnClickListener(v -> showCustomTimePicker(endTimeInput));

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void showDatePickerDialog(EditText dateInput) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) ->
                        dateInput.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                year, month, day);
        datePickerDialog.show();
    }

    private void showCustomTimePicker(EditText timeInput) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_time_picker);

        NumberPicker hourPicker = dialog.findViewById(R.id.hour_picker);
        NumberPicker minutePicker = dialog.findViewById(R.id.minute_picker);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);

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


    private void saveEvent() {
        String title = titleInput.getText().toString();
        String location = locationInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String sdate = startDateInput.getText().toString();
        String stime = startTimeInput.getText().toString();
        String edate = endDateInput.getText().toString();
        String etime = endTimeInput.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        try {
            Date startDateTime = sdate.isEmpty() || stime.isEmpty() ? null : dateTimeFormat.parse(sdate + " " + stime);
            Date endDateTime = edate.isEmpty() || etime.isEmpty() ? null : dateTimeFormat.parse(edate + " " + etime);

            if (sdate.isEmpty() || stime.isEmpty() || edate.isEmpty() || etime.isEmpty()) {
                Toast.makeText(EditEventActivity.this, "날짜와 시간을 입력해주세요.", Toast.LENGTH_LONG).show();
            } else if (endDateTime.before(startDateTime)) {
                Toast.makeText(EditEventActivity.this, "종료 날짜가 시작 날짜보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            } else {
                if (isEditing) {
                    new UpdateEventTask(eventId, title, location, description, startDateTime, endDateTime).execute();
                } else {
                    new AddEventTask(title, location, description, startDateTime, endDateTime).execute();
                }
            }
        } catch (ParseException e) {
            Toast.makeText(EditEventActivity.this, "날짜 형식이 잘못되었습니다. 올바른 형식으로 입력해주세요.", Toast.LENGTH_LONG).show();
        }
    }


    private class AddEventTask extends AsyncTask<Void, Void, Boolean> {
        private String title;
        private String location;
        private String description;
        private Date startDateTime;
        private Date endDateTime;

        public AddEventTask(String title, String location, String description, Date startDateTime, Date endDateTime) {
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
                    Event event = new Event()
                            .setSummary(title)
                            .setLocation(location)
                            .setDescription(description)
                            .setStart(new EventDateTime().setDateTime(new DateTime(startDateTime)).setTimeZone("Asia/Seoul"))
                            .setEnd(new EventDateTime().setDateTime(new DateTime(endDateTime)).setTimeZone("Asia/Seoul"));

                    mService.events().insert(calendarID, event).execute();
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
                Toast.makeText(EditEventActivity.this, "이벤트가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditEventActivity.this, "이벤트 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
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
                    event.setStart(new EventDateTime().setDateTime(new DateTime(startDateTime)).setTimeZone("Asia/Seoul"));
                    event.setEnd(new EventDateTime().setDateTime(new DateTime(endDateTime)).setTimeZone("Asia/Seoul"));

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
                Toast.makeText(EditEventActivity.this, "이벤트가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditEventActivity.this, "이벤트 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
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
