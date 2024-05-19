package android.org.firebasetest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WriteDiaryActivity extends AppCompatActivity {

    private EditText editTextDate, editTextWeather, editTextFeeling, editTextLocation, editTextContent;
    private Button buttonSaveDiary;
    private DiaryManager diaryManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

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

        mAuth = FirebaseAuth.getInstance();
        diaryManager = new DiaryManager();

        editTextDate = findViewById(R.id.editTextDate);
        editTextWeather = findViewById(R.id.editTextWeather);
        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveDiary = findViewById(R.id.buttonSaveDiary);

        buttonSaveDiary.setOnClickListener(v -> saveDiary());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void saveDiary() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String date = editTextDate.getText().toString().trim();
            String weather = editTextWeather.getText().toString().trim();
            String feeling = editTextFeeling.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();
            String diaryId = diaryManager.getDatabase().push().getKey(); // Generate unique ID for the memo

            Diary diary = new Diary(diaryId, userId, date, weather, feeling, location, null, null, content);
            diaryManager.saveDiary(userId, diary);
            Toast.makeText(this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after save
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show();
        }
    }
}