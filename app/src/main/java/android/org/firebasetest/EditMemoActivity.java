package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditMemoActivity extends AppCompatActivity {
    private EditText editTextFeeling, editTextBodyText;
    private Button buttonSaveChanges;
    private DatabaseReference databaseReference;
    private String memoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextBodyText = findViewById(R.id.editTextBodyText);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        memoId = getIntent().getStringExtra("memoId"); // Receive the Memo ID
        databaseReference = FirebaseDatabase.getInstance().getReference("memos").child(memoId);

        // Load memo details from the database or intent
        loadMemoDetails();

        buttonSaveChanges.setOnClickListener(v -> {
            saveMemoChanges();
        });
    }

    private void loadMemoDetails() {
        // Here you would normally load data from Firebase, but since we are assuming it's passed as an intent extra
        // This is just for demonstration assuming data is directly available
        Memo memo = getIntent().getParcelableExtra("memo");
        if (memo != null) {
            editTextFeeling.setText(memo.getFeeling());
            editTextBodyText.setText(memo.getBodyText());
        } else {
            Toast.makeText(this, "Failed to load memo details", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMemoChanges() {
        String updatedFeeling = editTextFeeling.getText().toString().trim();
        String updatedBodyText = editTextBodyText.getText().toString().trim();

        if (updatedFeeling.isEmpty() || updatedBodyText.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare updates map
        Map<String, Object> updates = new HashMap<>();
        updates.put("feeling", updatedFeeling);
        updates.put("bodyText", updatedBodyText);

        // Update only the feeling and bodyText fields in the existing database entry
        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditMemoActivity.this, "Memo updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity
                })
                .addOnFailureListener(e -> Toast.makeText(EditMemoActivity.this, "Failed to update memo", Toast.LENGTH_SHORT).show());
    }

}
