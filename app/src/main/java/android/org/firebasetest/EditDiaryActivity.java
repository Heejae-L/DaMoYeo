package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditDiaryActivity extends AppCompatActivity {

    private EditText editTextDate, editTextWeather, editTextFeeling, editTextLocation, editTextContent;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private String userId;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        diary = getIntent().getParcelableExtra("diary");

        initializeUI();

        if (diary == null) {
            Toast.makeText(this, "Essential data missing", Toast.LENGTH_LONG).show();
            finish(); // Close activity if data is not present
        } else {
            fillDiaryData();
        }

        buttonSave.setOnClickListener(v -> saveDiary());
    }


    private void initializeUI() {
        editTextDate = findViewById(R.id.editTextDate);
        editTextWeather = findViewById(R.id.editTextWeather);
        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSave = findViewById(R.id.buttonSave);

        databaseReference = FirebaseDatabase.getInstance().getReference("diaries").child(diary.getAuthorId());
    }

    private void fillDiaryData() {
        editTextDate.setText(diary.getDate());
        editTextWeather.setText(diary.getWeather());
        editTextFeeling.setText(diary.getFeeling());
        editTextLocation.setText(diary.getLocation());
        editTextContent.setText(diary.getContent());
    }

    private void saveDiary() {
        if (diary == null) {
            Toast.makeText(this, "Invalid or missing data", Toast.LENGTH_SHORT).show();
            return; // Prevent further execution if critical data is missing
        }

        String date = editTextDate.getText().toString().trim();
        String weather = editTextWeather.getText().toString().trim();
        String feeling = editTextFeeling.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        // Update the diary at the specific diaryId
        DatabaseReference diaryRef = databaseReference.child(diary.getDiaryId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("date", date);
        updates.put("weather", weather);
        updates.put("feeling", feeling);
        updates.put("location", location);
        updates.put("content", content);

        diaryRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditDiaryActivity.this, "Diary updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity
                })
                .addOnFailureListener(e -> Toast.makeText(EditDiaryActivity.this, "Failed to update Diary", Toast.LENGTH_SHORT).show());
    }



}
