package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {
    final DatabaseReference database;

    public GroupManager() {
        database = FirebaseDatabase.getInstance().getReference("groups");
    }

    public void saveGroup(Group group) {
        database.child(group.getGroupId()).setValue(group);
    }

    public void fetchGroups(ValueEventListener listener) {
        database.addListenerForSingleValueEvent(listener);
    }

    public void deleteGroup(String groupId) {
        database.child(groupId).removeValue();
    }

    public void fetchGroupsForUser(String userId, ValueEventListener listener) {
        // This method should fetch all groups where the userId is included in the memberIds list
        database.orderByChild("memberIds/" + userId).equalTo(true).addListenerForSingleValueEvent(listener);
    }

    public void addUserToGroup(String groupId, String userId, ValueEventListener completionListener) {
        DatabaseReference groupRef = database.child(groupId).child("memberIds");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> members = dataSnapshot.getValue(List.class);
                if (members == null) {
                    members = new ArrayList<>();
                }
                if (!members.contains(userId)) {
                    members.add(userId);
                    groupRef.setValue(members).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            completionListener.onDataChange(dataSnapshot);
                        } else {
                            completionListener.onCancelled(DatabaseError.fromException(task.getException()));
                        }
                    });
                } else {
                    // User is already in the group, handle this case if needed
                    completionListener.onDataChange(dataSnapshot);
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
