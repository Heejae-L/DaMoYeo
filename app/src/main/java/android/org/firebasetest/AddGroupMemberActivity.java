package android.org.firebasetest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddGroupMemberActivity extends AppCompatActivity {
    private EditText editTextMemberEmail;
    private Button buttonAddMember;
    private DatabaseReference databaseReference, usersReference;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        editTextMemberEmail = findViewById(R.id.editTextMemberId);
        buttonAddMember = findViewById(R.id.buttonAddMember);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "Group ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("memberIds");
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        buttonAddMember.setOnClickListener(v -> {
            String memberEmail = editTextMemberEmail.getText().toString().trim();
            if (!memberEmail.isEmpty()) {
                findUserIdByEmail(memberEmail);
            } else {
                Toast.makeText(this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findUserIdByEmail(String email) {
        Query query = usersReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        addMemberToGroup(userId);
                    }
                } else {
                    Toast.makeText(AddGroupMemberActivity.this, "No user found with that email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddGroupMemberActivity.this, "Failed to find user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMemberToGroup(String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(userId, true);
        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddGroupMemberActivity.this, "Member added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddGroupMemberActivity.this, "Failed to add member", Toast.LENGTH_SHORT).show());
    }
}
