package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class NotificationManager {
    private final DatabaseReference database;

    public NotificationManager() {
        database = FirebaseDatabase.getInstance().getReference("notifications");
    }

    public void saveNotification(Notification notification) {
        database.child(notification.getNotificationId()).setValue(notification);
    }

    public void fetchNotifications(ValueEventListener listener) {
        database.addListenerForSingleValueEvent(listener);
    }
}
