package android.org.firebasetest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewInvitationActivity extends AppCompatActivity {
    private ListView listViewInvitations;
    private DatabaseReference invitationsReference;
    private String groupId;
    private List<String> invitationDetailsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitations);



        listViewInvitations = findViewById(R.id.listViewInvitations);
        invitationDetailsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, invitationDetailsList);
        listViewInvitations.setAdapter(adapter);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "Group ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        invitationsReference = FirebaseDatabase.getInstance().getReference("invitations");
        loadInvitations();
    }

    private void loadInvitations() {
        invitationsReference.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        invitationDetailsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = snapshot.getValue(Invitation.class);
                            if (invitation != null) {
                                String detail = "Email: " + invitation.getInviteeId() + "\nStatus: " + invitation.getStatus();
                                invitationDetailsList.add(detail);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ViewInvitationActivity.this, "Failed to load invitations: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
