package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class InvitationManager {
    private final DatabaseReference database;

    public InvitationManager() {
        database = FirebaseDatabase.getInstance().getReference("invitations");
    }

    public void saveInvitation(Invitation invitation) {
        database.child(invitation.getInvitationId()).setValue(invitation);
    }

    public void deleteInvitation(String invitationId) {
        database.child(invitationId).removeValue();
    }

    public void fetchInvitationsForUser(String userId, ValueEventListener listener) {
        database.orderByChild("inviteeId").equalTo(userId).addListenerForSingleValueEvent(listener);
    }

    public void acceptInvitation(String invitationId, ValueEventListener completionListener) {
        DatabaseReference invitationRef = database.child(invitationId);
        invitationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Invitation invitation = dataSnapshot.getValue(Invitation.class);
                if (invitation != null) {
                    // You could update the invitation status here if needed
                    // Example: invitation.setStatus("accepted");
                    invitationRef.setValue(invitation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            completionListener.onDataChange(dataSnapshot);
                        } else {
                            completionListener.onCancelled(DatabaseError.fromException(task.getException()));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completionListener.onCancelled(databaseError);
            }
        });
    }

    public DatabaseReference getDatabaseReference() {
        // Provide a safe, read-only access to the database reference if needed
        return database;
    }
}
