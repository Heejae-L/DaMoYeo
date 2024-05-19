package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    private TextView titleTextView, dateTextView, feelingTextView, locationTextView, weatherTextView, contentTextView;
    private DiaryManager diaryManager;
    private String diaryDate;
    private Button editDiaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // 뒤로가기
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);  // Toolbar를 액티비티의 앱 바로 설정합니다.

        // 뒤로가기 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로가기 버튼이 클릭되면 현재 액티비티를 종료합니다.
                finish();
            }
        });

        titleTextView = findViewById(R.id.title);
        dateTextView = findViewById(R.id.date);
        feelingTextView = findViewById(R.id.feeling);
        locationTextView = findViewById(R.id.location);
        weatherTextView = findViewById(R.id.weather);
        contentTextView = findViewById(R.id.content);
        editDiaryButton = findViewById(R.id.buttonEditDiary);

        diaryManager = new DiaryManager();
        Diary diary = getIntent().getParcelableExtra("diary");

        if (diary != null) {
            displayDiary(diary);
        } else {
            Toast.makeText(this, "No diary date provided", Toast.LENGTH_SHORT).show();
        }
        editDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this, EditDiaryActivity.class);
                intent.putExtra("diary", diary);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void displayDiary(Diary diary) {
        titleTextView.setText(diary.getDate()); // assuming title is date for simplicity
        dateTextView.setText(diary.getDate());
        feelingTextView.setText("Feeling: " + diary.getFeeling());
        locationTextView.setText("Location: " + diary.getLocation());
        weatherTextView.setText("Weather: " + diary.getWeather());
        contentTextView.setText(diary.getContent());
    }
}
