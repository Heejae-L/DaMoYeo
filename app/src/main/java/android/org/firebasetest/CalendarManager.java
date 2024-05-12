package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CalendarManager {
    private final DatabaseReference database;

    public CalendarManager() {
        database = FirebaseDatabase.getInstance().getReference("calendars");
    }

    public void saveCalendar(Calendar calendar) {
        database.child(calendar.getCalendarId()).setValue(calendar);
    }

    public void fetchCalendars(ValueEventListener listener) {
        database.addListenerForSingleValueEvent(listener);
    }
}
