package android.org.firebasetest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteDiaryActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTitle, editTextWeather, editTextFeeling, editTextLocation, editTextContent;
    private Button buttonSaveDiary;
    private DiaryManager diaryManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        mAuth = FirebaseAuth.getInstance();
        diaryManager = new DiaryManager();

        editTextDate = findViewById(R.id.editTextDate);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextWeather = findViewById(R.id.editTextWeather);
        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveDiary = findViewById(R.id.buttonSaveDiary);

        editTextDate.setText(getCurrentDate());

        buttonSaveDiary.setOnClickListener(v -> saveDiary());

    }

    private void saveDiary() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String title = editTextTitle.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String weather = editTextWeather.getText().toString().trim();
            String feeling = editTextFeeling.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();
            String diaryId = diaryManager.getDatabase().push().getKey(); // Generate unique ID for the memo

            Diary diary = new Diary(diaryId, userId, date,title, weather, feeling, location, null, null, content);
            diaryManager.saveDiary(userId, diary);
            Toast.makeText(this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after save
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show();
        }
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}