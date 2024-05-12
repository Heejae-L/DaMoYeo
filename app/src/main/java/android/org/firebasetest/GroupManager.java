package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class GroupManager {
    private final DatabaseReference database;

    public GroupManager() {
        database = FirebaseDatabase.getInstance().getReference("groups");
    }

    public void saveGroup(Group group) {
        database.child(group.getGroupId()).setValue(group);
    }

    public void fetchGroups(ValueEventListener listener) {
        database.addListenerForSingleValueEvent(listener);
    }
}
