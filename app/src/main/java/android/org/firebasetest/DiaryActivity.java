package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {

    private TextView titleTextView, locationTextView, contentTextView;
    private DatePicker dateDatePicker;
    private ImageView weatherImageView, moodImageView;
    private ImageView voiceImage;
    private DiaryManager diaryManager;
    private String diaryDate;
    private Button editDiaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        titleTextView = findViewById(R.id.title);
        dateDatePicker = findViewById(R.id.date);
        locationTextView = findViewById(R.id.location);
        contentTextView = findViewById(R.id.content);
        voiceImage = findViewById(R.id.recorded_voice);
        editDiaryButton = findViewById(R.id.buttonEditDiary);

        weatherImageView = findViewById(R.id.selected_weather);
        moodImageView = findViewById(R.id.selected_mood);

        diaryManager = new DiaryManager();
        Diary diary = getIntent().getParcelableExtra("diary");

        if (diary != null) {
            displayDiary(diary);
        } else {
            Toast.makeText(this, "No diary date provided", Toast.LENGTH_SHORT).show();
        }
        String diaryId = diary.getDiaryId();
        Log.e("DiaryActvity1","diaryId "+diaryId);
        editDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this, CreateDiaryActivity.class);
                intent.putExtra("diary", diary);
                Log.e("DiaryActivity2","diaryId "+diaryId );
                intent.putExtra("diaryId",diaryId);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void displayDiary(Diary diary) {
        titleTextView.setText(diary.getTitle()); // assuming title is date for simplicity
        //dateTextView.setText(diary.getDate());
        Log.e("date","date"+diary.getDate());
        //feelingTextView.setText("Feeling: " + diary.getFeeling());
        locationTextView.setText("Location: " + diary.getLocation());
        //weatherTextView.setText("Weather: " + diary.getWeather());
        setDateToDatePicker(diary.getDate()); // DatePicker에 날짜 설정
        contentTextView.setText(diary.getContent());

        if (diary.getWeatherImageId() != 0) {
            weatherImageView.setImageResource(diary.getWeatherImageId());
        }

        // 기분 이미지 설정
        if (diary.getMoodImageId() != 0) {
            moodImageView.setImageResource(diary.getMoodImageId());
        }

        //displayImages(diary); // 이미지 불러와서 표시
    }

    /*
    private void displayImages(Diary diary) {
        Log.e("displayImages", "displayImages start");
        if (diary.getImageUrls() != null && diary.getImageUrls().size() > 0) {
            List<String> imageUrls = diary.getImageUrls();
            if (imageUrls.size() > 0 && imageUrls.get(0) != null)
                Glide.with(this).load(imageUrls.get(0)).into(selectedImg1);
            if (imageUrls.size() > 1 && imageUrls.get(1) != null)
                Glide.with(this).load(imageUrls.get(1)).into(selectedImg2);
            if (imageUrls.size() > 2 && imageUrls.get(2) != null)
                Glide.with(this).load(imageUrls.get(2)).into(selectedImg3);
        }
    }

     */


    private void setDateToDatePicker(String date) {
        Log.e("setDateToDatePicker", "setDateToDatePicker start");
        // 날짜 형식은 "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date dateObject = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            if (dateObject != null) {
                calendar.setTime(dateObject);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                dateDatePicker.updateDate(year, month, day);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

}