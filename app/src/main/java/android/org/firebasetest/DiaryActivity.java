package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
