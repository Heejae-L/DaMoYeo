package android.org.firebasetest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMyInvitationsActivity extends AppCompatActivity {
    private ListView listViewInvitations;
    private DatabaseReference databaseReference;
    private List<Invitation> invitationList;
    private ArrayAdapter<Invitation> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_invitations);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        listViewInvitations = findViewById(R.id.listViewInvitations);
        invitationList = new ArrayList<>();
        adapter = new InvitationAdapter(invitationList);
        listViewInvitations.setAdapter(adapter);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadInvitations(currentUserId);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void loadInvitations(String userId) {
        databaseReference.child("invitations").orderByChild("inviteeId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        invitationList.clear(); // 기존 목록을 클리어
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = snapshot.getValue(Invitation.class);
                            if (invitation != null && !invitation.isAccepted()) { // 초대 수락 여부 확인
                                invitationList.add(invitation); // 수락되지 않은 초대만 목록에 추가
                            }
                        }
                        adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경을 알림
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ViewMyInvitationsActivity.this, "Failed to load invitations: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private class InvitationAdapter extends ArrayAdapter<Invitation> {
        InvitationAdapter(List<Invitation> invitations) {
            super(ViewMyInvitationsActivity.this, R.layout.invitation_item, invitations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.invitation_item, parent, false);
            }

            TextView textViewDetails = convertView.findViewById(R.id.textViewDetails);
            Button buttonAccept = convertView.findViewById(R.id.buttonAccept);
            Button buttonDecline = convertView.findViewById(R.id.buttonDecline);

            final Invitation invitation = getItem(position);

            // Clear the existing text
            textViewDetails.setText("");

            // Fetch and set the group and inviter names
            fetchGroupName(invitation.getGroupId(), textViewDetails);
            fetchInviterName(invitation.getInviterId(), textViewDetails);

            // Update status
            textViewDetails.append("Status: " + invitation.getStatus() + "\n");

            buttonAccept.setOnClickListener(view -> updateInvitation(invitation, true));
            buttonDecline.setOnClickListener(view -> updateInvitation(invitation, false));

            return convertView;
        }

    }
    private void fetchGroupName(String groupId, final TextView textView) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("title");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupName = dataSnapshot.getValue(String.class);
                if (groupName != null) {
                    // Update the textView part of the invitation item
                    textView.setText(textView.getText() + "Group: " + groupName + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewMyInvitationsActivity.this, "Failed to fetch group name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchInviterName(String inviterId, final TextView textView) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(inviterId).child("name");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String inviterName = dataSnapshot.getValue(String.class);
                if (inviterName != null) {
                    // Append to the current text in textView
                    textView.setText(textView.getText() + "From: " + inviterName + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewMyInvitationsActivity.this, "Failed to fetch inviter name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInvitation(Invitation invitation, boolean accept) {
        DatabaseReference inviteRef = databaseReference.child("invitations").child(invitation.getInvitationId());
        DatabaseReference groupMembersRef = databaseReference.child("groups").child(invitation.getGroupId()).child("memberIds");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (accept) {
            inviteRef.child("accepted").setValue(true);
            inviteRef.child("status").setValue("Accepted").addOnSuccessListener(aVoid -> {
                // Add user to the group's member list
                groupMembersRef.child(currentUserId).setValue(true)
                        .addOnSuccessListener(aVoid2 -> Toast.makeText(ViewMyInvitationsActivity.this, "You have been added to the group and invitation accepted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ViewMyInvitationsActivity.this, "Failed to add to group", Toast.LENGTH_SHORT).show());
            });
        } else {
            inviteRef.child("accepted").setValue(false);
            inviteRef.child("status").setValue("Declined");
            Toast.makeText(this, "Invitation declined", Toast.LENGTH_SHORT).show();
        }
    }
}
