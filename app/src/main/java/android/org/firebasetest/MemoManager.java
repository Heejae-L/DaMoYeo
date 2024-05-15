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
    private DatabaseReference database; // Make it private to encapsulate

    public MemoManager() {
        database = FirebaseDatabase.getInstance().getReference("memos");
    }
    public DatabaseReference getDatabase() {
        return database;
    }
    public void saveMemo(Memo memo) {
        database.child(memo.getMemoId()).setValue(memo);
    }

    public void deleteMemo(String memoId) {
        database.child(memoId).removeValue();
    }

    public void fetchMemos(MemosCallback callback) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Memo> memos = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Memo memo = snapshot.getValue(Memo.class);
                    memos.add(memo);
                }
                callback.onMemosRetrieved(memos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // 메모 그룹별로 가져오기
    public void fetchMemosByGroupId(String groupId, MemosCallback callback) {
        database.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Memo> groupMemos = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Memo memo = snapshot.getValue(Memo.class);
                    groupMemos.add(memo);
                }
                callback.onMemosRetrieved(groupMemos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // 특정 메모 상세 정보 가져오기
    public void fetchMemoById(String memoId, MemoCallback callback) {
        database.child(memoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Memo memo = dataSnapshot.getValue(Memo.class);
                    callback.onMemoRetrieved(memo);
                } else {
                    callback.onError(new Exception("Memo not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface MemosCallback {
        void onMemosRetrieved(List<Memo> memos);
        void onError(Exception exception);
    }

    public interface MemoCallback {
        void onMemoRetrieved(Memo memo);
        void onError(Exception exception);
    }
}
