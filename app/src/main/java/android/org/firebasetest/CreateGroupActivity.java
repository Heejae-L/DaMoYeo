package android.org.firebasetest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription;
    private Button buttonCreateGroup;
    private GroupManager groupManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mAuth = FirebaseAuth.getInstance();
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
        Toast.makeText(CreateGroupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close this activity after group creation
    }
}
