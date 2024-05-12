package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final DatabaseReference database;

    public UserManager() {
        database = FirebaseDatabase.getInstance().getReference("users");
    }

    public void saveUser(User user) {
        database.child(user.getUserId()).setValue(user);
    }

    public void fetchUsers(UsersCallback callback) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }
                callback.onUsersRetrieved(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // 새로운 메소드를 추가합니다.
    public void fetchUserById(String userId, UserCallback callback) {
        database.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    callback.onUserRetrieved(user);
                } else {
                    callback.onError(new Exception("User not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface UsersCallback {
        void onUsersRetrieved(List<User> users);
        void onError(Exception exception);
    }

    // 새로운 callback 인터페이스를 정의합니다.
    public interface UserCallback {
        void onUserRetrieved(User user);
        void onError(Exception exception);
    }
}
