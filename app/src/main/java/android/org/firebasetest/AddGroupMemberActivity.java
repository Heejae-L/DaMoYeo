package android.org.firebasetest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddGroupMemberActivity extends AppCompatActivity {
    private EditText editTextMemberEmail;
    private Button buttonAddMember;
    private DatabaseReference databaseReference, usersReference, invitationsReference;

    private String groupId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        editTextMemberEmail = findViewById(R.id.editTextMemberId);
        buttonAddMember = findViewById(R.id.buttonAddMember);

        userId = getIntent().getStringExtra("userId");
        groupId = getIntent().getStringExtra("groupId");

        if (groupId == null) {
            Toast.makeText(this, "Group ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to the members of the group
        databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("memberIds");
        // Reference to all users for searching by email
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        // Reference to invitations
        invitationsReference = FirebaseDatabase.getInstance().getReference("invitations");

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
                        String inviteeId = snapshot.getKey();
                        inviteUserToGroup(userId, email, inviteeId);
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

    private void inviteUserToGroup(String inviterId, String email, String inviteeId) {
        String invitationId = invitationsReference.push().getKey(); // Create a unique invitation ID
        Map<String, Object> invitationData = new HashMap<>();
        invitationData.put("invitationId", invitationId);
        invitationData.put("groupId", groupId);
        invitationData.put("inviterId", inviterId);
        invitationData.put("inviteeId", inviteeId);
        invitationData.put("email", email); // Optional: Store email as well in the invitation
        invitationData.put("date", getCurrentDate()); // Replace with the actual current date
        invitationData.put("status", "pending"); // Initial status of the invitation
        invitationData.put("accepted", false); // Initially, the invitation is not accepted

        invitationsReference.child(invitationId).setValue(invitationData)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddGroupMemberActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddGroupMemberActivity.this, "Failed to send invitation", Toast.LENGTH_SHORT).show());
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
