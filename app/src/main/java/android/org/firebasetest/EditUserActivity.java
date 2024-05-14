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

public class EditUserActivity extends AppCompatActivity {
    private EditText editTextName, editTextAge, editTextBio;
    private Button buttonSaveChanges;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info); // Ensure you have this layout

        // Initialize fields
        editTextName = findViewById(R.id.editTextUserName);
        editTextAge = findViewById(R.id.editTextUserAge);
        editTextBio = findViewById(R.id.editTextUserBio);
        buttonSaveChanges = findViewById(R.id.buttonSaveUserInfo);

        // Get user ID from intent or saved state
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Error: No user ID provided.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set reference to user's data in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Set up the button click listener
        buttonSaveChanges.setOnClickListener(v -> saveUserChanges());
    }

    private void saveUserChanges() {
        String name = editTextName.getText().toString().trim();
        String bio = editTextBio.getText().toString().trim();
        int age;
        try {
            age = Integer.parseInt(editTextAge.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age input", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Name and bio cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        updates.put("age", age);

        // Update children at this DatabaseReference
        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditUserActivity.this, "User info updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity
                })
                .addOnFailureListener(e -> Toast.makeText(EditUserActivity.this, "Failed to update user info", Toast.LENGTH_SHORT).show());
    }
}
