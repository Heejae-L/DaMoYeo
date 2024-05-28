package android.org.firebasetest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;

import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class CalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    TextView monthYearText;//년월 텍스트뷰

    //Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체

    private com.google.api.services.calendar.Calendar mService = null;

    // Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
    private  int mID = 0;

    GoogleAccountCredential mCredential;
    private TextView mStatusText;
    private TextView mResultText;
    private Button mGetEventButton;
    private Button mAddEventButton;
    private Button mAddCalendarButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private CalendarAdapter adapter;
    private DateInfo selectedDateInfo;  // 선택된 날짜 저장

    private DateTime selectedDate; // 선택된 날짜를 DateTime 객체로 저장
    private RecyclerView recyclerViewEvents;
    private EventDetailsAdapter eventDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

        //초기화
        monthYearText=findViewById(R.id.monthYearText);
        ImageButton prebtn = findViewById(R.id.pre_btn);
        ImageButton nextbtn = findViewById(R.id.next_btn);
        Button changeAccountButton = findViewById(R.id.change_account_button);

        recyclerViewEvents= findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        //현재 날짜
        CalendarUtil.selectedDate = Calendar.getInstance();

        //화면 설정
        setMonthView();

        //이전달 버튼 이벤트
        prebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //현재 월-1 변수에 담기
                CalendarUtil.selectedDate.add(Calendar.MONTH, -1);
                setMonthView();
            }
        });
        //다음달 버튼 이벤트
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //현재 월+1 변수에 담기
                CalendarUtil.selectedDate.add(Calendar.MONTH,+1);
                setMonthView();
            }
        });

        changeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로직 구현
                clearAccountFromPreferences();
                resetGoogleAccountCredential();
                promptUserToSelectAccount();
            }
        });

        mAddCalendarButton = (Button) findViewById(R.id.button_main_add_calendar);
        mAddEventButton = (Button) findViewById(R.id.button_main_add_event);
        mGetEventButton = (Button) findViewById(R.id.button_main_get_event);

        //버튼 클릭으로 동작 테스트
        mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tryCreateCalendar();
                mAddCalendarButton.setEnabled(false);
                //mStatusText.setText("");
                mID = 1;           //캘린더 생성
                getResultsFromApi();
                mAddCalendarButton.setEnabled(true);
            }
        });

        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddEventButton.setEnabled(false);
                showAddEventDialog();  // 이벤트 제목을 입력받는 대화상자를 표시
                mID = 2;        //이벤트 생성
                getResultsFromApi();
                mAddEventButton.setEnabled(true);
            }
        });

        mGetEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetEventButton.setEnabled(false);
                mID = 3;        //이벤트 가져오기
                selectedDate = new DateTime(System.currentTimeMillis());  // 현재 날짜 설정, 실제 앱에서는 사용자가 선택한 날짜를 이용
                getResultsFromApi();
                mGetEventButton.setEnabled(true);
            }
        });

        // Google Calendar API 호출중에 표시되는 ProgressDialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");

        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

    }//oncreate

    //날짜 타입 설정 (n월 yyyy)
    private String monthYearFromDate(Calendar calendar){
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);

        String monthYear = (month+1) + "월" + year;
        return monthYear;
    }

    //화면 설정
    private void setMonthView(){
        //년월 텍스트뷰 셋팅
        monthYearText.setText(monthYearFromDate(CalendarUtil.selectedDate));
        //해당 월의 날짜 가져오기
        ArrayList<Date> dayList = daysInMonthArray();

        if (adapter == null) {
            adapter = new CalendarAdapter(this, dayList);
            adapter.setDateSelectedListener((year, month, day) -> {
                selectedDateInfo = new DateInfo(year, month, day); // 날짜 정보 저장
                String dateMessage = year + "년 " + month + "월 " + day + "일 선택됨";
                //Toast.makeText(MainActivity.this, dateMessage, Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Selected date: " + year + "-" + month + "-" + day);
            });
            recyclerViewEvents.setAdapter(adapter);
        } else {
            adapter.updateDayList(dayList);
        }

        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerViewEvents.setLayoutManager(manager);
    }

    //날짜 생성
    private ArrayList<Date> daysInMonthArray(){
        ArrayList<Date> dayList = new ArrayList<>();

        //날짜 복사해서 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();
        //1일로 세팅
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        //요일 가져와서 -1 =>일요일:1, 월요일:2
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        //날짜 셋팅(-5일 전)
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        //42전까지 반복
        while (dayList.size()<42){
            //리스트에 날짜 등록
            dayList.add(monthCalendar.getTime());
            //1일씩 늘린 날짜로 변경
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dayList;
    }

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     *
     * 사전 조건
     *     - Google Play Services 설치
     *     - 유효한 구글 계정 선택
     *     - 안드로이드 디바이스에서 인터넷 사용 가능
     *
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */

    private String getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Log.e("CalendarAPI", "Google 계정이 선택되지 않았습니다.");
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mStatusText.setText("No network connection available.");
        } else {
            proceedWithCalendarApi();
        }
        return null;
    }

    private void proceedWithCalendarApi() {
        Log.d("CalendarAPI", "Using account: " + mCredential.getSelectedAccountName());
        // Google Calendar API 호출
        new MakeRequestTask(this, mCredential).execute();
    }


    //안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    //Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해 대화상자를 보여줌.
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    //안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                CalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                Log.d("CalendarAPI", "Using saved account name: " + accountName);

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                if (mCredential.getSelectedAccountName() != null) {
                    Log.d("CalendarAPI", "Account set in credential: " + mCredential.getSelectedAccountName());
                    getResultsFromApi();
                } else {
                    Log.e("CalendarAPI", "Failed to set account in credential");
                }
            } else {
                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                Log.d("CalendarAPI", "No account name found, prompting user to choose account");

                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {
            Log.d("CalendarAPI", "Requesting GET_ACCOUNTS permission");


            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    //계정 정보 초기화
    private void clearAccountFromPreferences() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_ACCOUNT_NAME); // PREF_ACCOUNT_NAME는 계정 정보를 저장할 때 사용한 키입니다.
        editor.apply(); // 변경사항 적용
    }
    private void resetGoogleAccountCredential() {
        if (mCredential != null) {
            mCredential.setSelectedAccountName(null); // 계정 선택 초기화
        }
    }
    private void promptUserToSelectAccount() {
        chooseAccount(); // 사용자에게 계정 선택을 위한 다이얼로그 표시
    }







    //구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아올때 호출된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mStatusText.setText("앱을 실행시키려면 구글 플레이 서비스가 필요합니다." +
                            "구글 플레이 서비스를 설치 후 다시 실행하세요.");
                } else {
                    getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    //Android 6.0 (API 23) 이상에서 런타임 권한 요청시 결과를 리턴받음
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  //requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 승인한 경우 호출된다.
    @Override
    public void onPermissionsGranted(int requestCode, List<String> requestPermissionList) {
        // 아무일도 하지 않음
    }

    //EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 거부한 경우 호출된다.
    @Override
    public void onPermissionsDenied(int requestCode, List<String> requestPermissionList) {
        // 아무일도 하지 않음
    }


    //안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    //캘린더 이름에 대응하는 캘린더 ID를 리턴
    private String getCalendarID(String calendarTitle){

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                if ( calendarListEntry.getSummary().toString().equals(calendarTitle)) {
                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
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
            // 시간을 EditText에 설정
            timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이벤트 정보 입력");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_event_input, null);
        //해당 부분 dialogView를 다른걸로 바꾸면 아예 새창으로 띄울 수 있을듯?
        builder.setView(dialogView);


        final EditText titleInput = dialogView.findViewById(R.id.event_title_input);
        final EditText locationInput = dialogView.findViewById(R.id.event_location_input);
        final EditText descriptionInput = dialogView.findViewById(R.id.event_description_input);
        final EditText startdate = dialogView.findViewById(R.id.start_date);
        final EditText starttime = dialogView.findViewById(R.id.start_time);
        final EditText enddate = dialogView.findViewById(R.id.end_date);
        final EditText endtime = dialogView.findViewById(R.id.end_time);



        // 날짜 입력 필드에 DatePickerDialog 설정
        startdate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) ->
                            startdate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                    year, month, day);
            datePickerDialog.show();
        });

        // 시작 시간 입력 필드 설정
        starttime.setOnClickListener(v -> showCustomTimePicker(starttime));

        // 날짜 입력 필드에 DatePickerDialog 설정
        enddate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) ->
                            enddate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                    year, month, day);
            datePickerDialog.show();
        });

        // 종료 시간 입력 필드 설정
        endtime.setOnClickListener(v -> showCustomTimePicker(endtime));

        builder.setPositiveButton("OK", null); // 리스너를 null로 설정
        builder.setNegativeButton("Cancel", null); // 리스너를 null로 설정

        final AlertDialog dialog = builder.create();

        // 다이얼로그 보이기
        dialog.show();

        // 긍정적 버튼에 대한 리스너 직접 설정
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String location = locationInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String sdate = startdate.getText().toString();
            String stime = starttime.getText().toString();
            String edate = enddate.getText().toString();
            String etime = endtime.getText().toString();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            try {
                Date startDate = dateFormat.parse(sdate + " " + stime);
                Date endDate = dateFormat.parse(edate + " " + etime);

                if (startDate == null || endDate == null) {
                    Toast.makeText(CalendarActivity.this, "날짜와 시간 형식이 잘못되었습니다.", Toast.LENGTH_LONG).show();
                } else if (sdate.isEmpty() || stime.isEmpty() || edate.isEmpty() || etime.isEmpty()) {
                    Toast.makeText(CalendarActivity.this, "날짜와 시간을 입력해주세요.", Toast.LENGTH_LONG).show();
                } else if (endDate.before(startDate)) {
                    Toast.makeText(CalendarActivity.this, "종료 날짜가 시작 날짜보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    mID = 2;  // 이벤트 생성
                    new MakeRequestTask(CalendarActivity.this, mCredential, title, location, description, sdate, stime, edate, etime).execute();
                    dialog.dismiss(); // 모든 입력이 유효하면 다이얼로그 닫기
                }
            } catch (ParseException e) {
                Toast.makeText(CalendarActivity.this, "날짜 형식이 잘못되었습니다. 올바른 형식으로 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        });

        // 부정적 버튼에 대한 리스너 직접 설정
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(view -> {
            dialog.cancel();
            mAddEventButton.setEnabled(true);  // 사용자가 취소를 누르면 버튼을 다시 활성화
        });

    }

    // 날짜 선택 이벤트 처리
    public void onDateSelected(int year, int month, int day) {
        //Toast.makeText(this, "Selected: " + year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
    }


    //비동기적으로 Google Calendar API 호출
    private class MakeRequestTask extends AsyncTask<Void, Void, List<EventDetail>> {

        private Exception mLastError = null;
        private CalendarActivity mActivity;
        private String mTitle, mLocation, mDescription, msDate, msTime, meDate, meTime;  // 이벤트 제목, 위치, 설명을 저장할 필드 추가
        private GoogleAccountCredential mCredential;

        public MakeRequestTask(CalendarActivity activity, GoogleAccountCredential credential) {
            this(activity, credential, "", "", "", "", "", "", ""); // 기본값으로 빈 문자열을 사용
        }
        public MakeRequestTask(CalendarActivity activity, GoogleAccountCredential credential,
                               String title, String location, String description, String sdate, String stime, String edate, String etime) {

            mActivity = activity;

            mCredential = credential;
            mTitle = title;
            mLocation = location;
            mDescription = description;
            msDate = sdate;
            msTime = stime;
            meDate = edate;
            meTime = etime;


            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        @Override
        protected void onPreExecute() {
            if (!mActivity.isFinishing()) { // Activity가 종료되지 않았는지 확인
                mProgress.show(); // Activity가 종료되지 않은 경우에만 ProgressDialog를 표시
            }
        }


        //백그라운드에서 Google Calendar API 호출 처리
        @Override
        protected List<EventDetail> doInBackground(Void... params) {
            try {
                switch (mID) {
                    case 1:
                        createCalendar();
                        return null;
                    case 2:
                        addEvent();
                        return null;
                    case 3:
                        return getEvent();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }


        //CalendarTitle 이름의 캘린더에서 10개의 이벤트를 가져와 리턴
        private List<EventDetail> getEvent() throws IOException {
            // Calendar 객체를 사용하여 선택된 날짜 설정
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul"); // 예를 들어 서울 시간대
            Calendar cal = Calendar.getInstance(timeZone);

            cal.set(Calendar.YEAR, selectedDateInfo.getYear());
            cal.set(Calendar.MONTH, selectedDateInfo.getMonth() - 1); // Calendar.MONTH는 0에서 시작하므로 1을 빼줍니다.
            cal.set(Calendar.DAY_OF_MONTH, selectedDateInfo.getDay());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // 선택된 날짜의 자정 시간을 DateTime 객체로 변환
            DateTime startTime = new DateTime(cal.getTimeInMillis());

            // 선택된 날짜의 다음 날 자정 전까지
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            DateTime endTime = new DateTime(cal.getTimeInMillis());

            String calendarID = getCalendarID("CalendarTitle");
            if (calendarID == null) {
                throw new IOException("캘린더를 먼저 생성하세요.");
            }

            Events events = mService.events().list(calendarID)
                    .setMaxResults(10)
                    .setTimeMin(startTime)
                    .setTimeMax(endTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            List<EventDetail> eventDetails = new ArrayList<>();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                }
                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate();
                }

                // 한국 시간대에 맞게 포맷팅
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                String formattedStartTime = dateTimeFormat.format(new Date(start.getValue()));
                String formattedEndTime = dateTimeFormat.format(new Date(end.getValue()));

                eventDetails.add(new EventDetail(event.getSummary(), start, end, event.getId(), event.getLocation(), event.getDescription(), formattedStartTime, formattedEndTime));
            }
            return eventDetails;
        }


        //선택되어 있는 Google 계정에 새 캘린더를 추가한다.
        private String createCalendar() throws IOException {
//수정 : string으로 받아 타이틀 설정
            String ids = getCalendarID("CalendarTitle");

            if ( ids != null ){
                Log.d("CalendarAPI", "캘린더가 이미 생성되어 있습니다: ID = " + ids);

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary("CalendarTitle");

            // 캘린더의 시간대 설정
            calendar.setTimeZone("Asia/Seoul");

            // 구글 캘린더에 새로 만든 캘린더를 추가
            com.google.api.services.calendar.model.Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // 추가한 캘린더의 ID를 가져옴.
            String calendarId = createdCalendar.getId();

            Log.d("CalendarAPI", "캘린더 생성 성공: ID = " + calendarId);
            // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // 캘린더의 배경색을 파란색으로 표시  RGB
            calendarListEntry.setBackgroundColor("#0000ff");

            // 변경한 내용을 구글 캘린더에 반영
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();
            // 새로 추가한 캘린더의 ID를 리턴
            return "캘린더가 생성되었습니다.";
        }


        @Override
        protected void onPostExecute(List<EventDetail> eventDetails) {
            if (!mActivity.isFinishing() && !mActivity.isDestroyed() && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            if (eventDetails != null && !eventDetails.isEmpty()) {
                if (mID == 3) {  // 이벤트 정보 가져오기가 완료된 경우
                    Intent intent = new Intent(CalendarActivity.this, EventDetailsActivity.class);
                    intent.putExtra("YEAR", selectedDateInfo.getYear());
                    intent.putExtra("MONTH", selectedDateInfo.getMonth());
                    intent.putExtra("DAY", selectedDateInfo.getDay());
                    intent.putExtra("ACCOUNT_NAME", mCredential.getSelectedAccountName()); // 계정 정보 추가
                    // 이벤트 데이터를 직렬화하여 인텐트에 추가
                    ArrayList<EventDetail> eventDetailArrayList = new ArrayList<>(eventDetails);
                    intent.putParcelableArrayListExtra("eventDetails", eventDetailArrayList);
                    startActivity(intent);  // EventDetailsActivity 시작
                }
            } else {
                Toast.makeText(CalendarActivity.this, "다이어리 불러오기 완료.", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();

            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        CalendarActivity.REQUEST_AUTHORIZATION);
            }
        }
        private com.google.api.services.calendar.model.Calendar service;
        public void CalendarEventCreator(com.google.api.services.calendar.model.Calendar service) {
            this.service = service;
        }

        private String addEvent() {
            String calendarID = getCalendarID("CalendarTitle");
            if (calendarID == null) {
                return "캘린더를 먼저 생성하세요.";
            }

            Log.d("CalendarAPI", "캘린더 ID: " + calendarID); // 캘린더 ID 로깅

            String startdateTimeString = msDate + "T" + msTime + ":00+09:00"; // +09:00은 한국 표준시(KST)를 의미
            String enddateTimeString = meDate + "T" + meTime + ":00+09:00"; // +09:00은 한국 표준시(KST)를 의미

            DateTime startDateTime = new DateTime(startdateTimeString);
            DateTime endDateTime = new DateTime(enddateTimeString);

            Event event = new Event()
                    .setSummary(mTitle)                             //제목
                    .setLocation(mLocation)                         //위치
                    .setDescription(mDescription)                   //설명
                    .setStart(new EventDateTime().                  //시작 날짜 및 시간
                            setDateTime(startDateTime).
                            setTimeZone("Asia/Seoul"))
                    .setEnd(new EventDateTime().                    //종료 날짜 및 시간
                            setDateTime(endDateTime).
                            setTimeZone("Asia/Seoul"))
                    .setRecurrence(Arrays.asList("RRULE:FREQ=DAILY;COUNT=1"))
                    //.setAttendees(Arrays.asList(                  //참가자 추가
                    //        new EventAttendee().setEmail("lpage@example.com"),
                    //        new EventAttendee().setEmail("sbrin@example.com")))
                    .setReminders(new Event.Reminders().            //알람
                            setUseDefault(false).setOverrides(Arrays.asList(
                            new EventReminder().setMethod("email").setMinutes(24 * 60),
                            new EventReminder().setMethod("popup").setMinutes(10))));

            Log.d("CalendarAPI", "Attempting to create event: " + event.toString());
            try {
                Event createdEvent = mService.events().insert(calendarID, event).execute();
                Log.d("CalendarAPI", "이벤트 생성 성공: " + createdEvent.getHtmlLink());
                return "Event created: " + createdEvent.getHtmlLink();
            } catch (Exception e) {
                Log.e("CalendarAPI", "이벤트 생성 실패", e);
                return "이벤트 생성에 실패했습니다: " + e.getMessage();
            }
        }


    }
}

