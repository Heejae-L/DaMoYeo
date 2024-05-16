package android.org.firebasetest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageResultLauncher;
    private EditText editTextName, editTextAge, editTextBio;
    private ImageView imageViewProfile;
    private Button buttonSelectProfileImage, buttonSaveChanges;
    private DatabaseReference databaseReference;
    private String userId;
    private Uri imageUri;

    private ProfileImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        editTextName = findViewById(R.id.editTextUserName);
        editTextAge = findViewById(R.id.editTextUserAge);
        editTextBio = findViewById(R.id.editTextUserBio);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonSelectProfileImage = findViewById(R.id.buttonUploadProfileImage);
        buttonSaveChanges = findViewById(R.id.buttonSaveUserInfo);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Error: No user ID provided.", Toast.LENGTH_SHORT).show();
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        loadUserData();  // Load existing user data

        imageManager = new ProfileImageManager();
        imageManager.loadProfileImage(this, imageViewProfile, userId);

        pickImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                imageViewProfile.setImageURI(imageUri);  // 선택한 이미지를 imageView에 설정
            }
        });

        buttonSelectProfileImage.setOnClickListener(v -> openGallery());
        buttonSaveChanges.setOnClickListener(v -> saveUserChanges());
    }

    private void loadUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    editTextName.setText(user.getName());
                    editTextAge.setText(String.valueOf(user.getAge()));
                    editTextBio.setText(user.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditUserActivity.this, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageResultLauncher.launch(intent);
    }

    private void saveUserChanges() {
        String name = editTextName.getText().toString().trim();
        String bio = editTextBio.getText().toString().trim();
        int age = Integer.parseInt(editTextAge.getText().toString().trim());  // Assume the input is always valid

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        updates.put("age", age);

        databaseReference.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditUserActivity.this, "User info updated successfully", Toast.LENGTH_SHORT).show();
            if (imageUri != null) {
                imageManager.saveProfileImage(this, imageUri, userId);
            }
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(EditUserActivity.this, "Failed to update user info", Toast.LENGTH_SHORT).show();
        });
    }
}
