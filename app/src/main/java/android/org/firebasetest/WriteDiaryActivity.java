package android.org.firebasetest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        mAuth = FirebaseAuth.getInstance();
        diaryManager = new DiaryManager();

        editTextDate = findViewById(R.id.editTextDate);
        editTextWeather = findViewById(R.id.editTextWeather);
        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveDiary = findViewById(R.id.buttonSaveDiary);

        buttonSaveDiary.setOnClickListener(v -> saveDiary());
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

            Diary diary = new Diary(userId, date, weather, feeling, location, null, null, content);
            diaryManager.saveDiary(userId, date, diary);
            Toast.makeText(this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after save
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show();
        }
    }
}
