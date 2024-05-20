package android.org.firebasetest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
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
    private Spinner spinnerGender;
    private ImageView imageViewProfile;
    private Button buttonSelectProfileImage, buttonSaveChanges;
    private DatabaseReference databaseReference;
    private String userId;
    private Uri imageUri;
    ArrayAdapter<CharSequence> adapter;
    private ProfileImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

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

        editTextName = findViewById(R.id.editTextUserName);
        editTextAge = findViewById(R.id.editTextUserAge);
        editTextBio = findViewById(R.id.editTextUserBio);
        spinnerGender = findViewById(R.id.spinnerUserGender);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonSelectProfileImage = findViewById(R.id.buttonUploadProfileImage);
        buttonSaveChanges = findViewById(R.id.buttonSaveUserInfo);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Error: No user ID provided.", Toast.LENGTH_SHORT).show();
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        loadUserData();  // Load existing user data

        imageManager = new ProfileImageManager();
        imageManager.loadProfileImage(this, imageViewProfile, userId);

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
                    if (user.getGender() != null) {
                        int spinnerPosition = adapter.getPosition(user.getGender());
                        spinnerGender.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditUserActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
        pickImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                imageViewProfile.setImageURI(imageUri);  // 선택한 이미지를 imageView에 설정
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
        int age = Integer.parseInt(editTextAge.getText().toString().trim());
        String gender = spinnerGender.getSelectedItem().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        updates.put("age", age);
        updates.put("gender", gender);

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
