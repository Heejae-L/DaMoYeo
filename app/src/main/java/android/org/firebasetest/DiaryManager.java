package android.org.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiaryManager {
    private final DatabaseReference database;

    public DiaryManager() {
        database = FirebaseDatabase.getInstance().getReference("diaries");
    }
    public DatabaseReference getDatabase() {
        return database;
    }
    public void saveMemo(Memo memo) {
        database.child(memo.getMemoId()).setValue(memo);
    }
    public void saveDiary(String userId,Diary diary) {
        database.child(userId).child(diary.getDiaryId()).setValue(diary);
    }
    public void deleteDiary(String userId, String diaryId) {
        database.child(userId).child(diaryId).removeValue();
    }


    public void fetchDiaries(String userId, DiariesCallback callback) {
        database.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Diary> diaries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Diary diary = snapshot.getValue(Diary.class);
                    diaries.add(diary);
                }
                callback.onDiariesRetrieved(diaries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface DiariesCallback {
        void onDiariesRetrieved(List<Diary> diaries);
        void onError(Exception exception);
    }
}
