package android.org.firebasetest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription;
    private Button buttonCreateGroup;
    private GroupManager groupManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        groupManager = new GroupManager();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
    }

    private void createGroup() {
        FirebaseUser user = mAuth.getCurrentUser();
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String userId = user.getUid();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(CreateGroupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = FirebaseDatabase.getInstance().getReference("groups").push().getKey();


        Map<String, Boolean> memberIds = new HashMap<>();
        memberIds.put(userId, true);

        Group newGroup = new Group(
                FirebaseDatabase.getInstance().getReference("groups").push().getKey(),
                description,
                title,
                "",
                memberIds
        );

        groupManager.saveGroup(newGroup);
        sendInitialMessage(groupId, userId);
        Toast.makeText(CreateGroupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close this activity after group creation
    }

    private void sendInitialMessage(String groupId, String userId) {
        Map<String, Object> message = new HashMap<>();
        message.put("username", "System");
        message.put("message", "채팅방이 개설되었습니다");
        message.put("timestamp", new java.util.Date());
        message.put("seenCount", 0); // 초기 메시지이므로 본 사람 수는 0

        db.collection("Chat").document(groupId).collection("Messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d("CreateGroupActivity", "Initial message sent successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateGroupActivity", "Error sending initial message", e);
                });

    }
}