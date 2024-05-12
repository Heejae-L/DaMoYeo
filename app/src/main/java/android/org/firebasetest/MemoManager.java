package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemoManager {
    private final DatabaseReference database;

    public MemoManager() {
        database = FirebaseDatabase.getInstance().getReference("memos");
    }

    public void saveMemo(Memo memo) {
        database.child(memo.getMemoId()).setValue(memo);
    }

    public void fetchMemos(ValueEventListener listener) {
        database.addListenerForSingleValueEvent(listener);
    }
}
