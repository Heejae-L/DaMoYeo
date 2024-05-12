package android.org.firebasetest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMemoActivity extends AppCompatActivity {
    private ListView listViewMemos;
    private ArrayAdapter<String> memoAdapter;
    private List<Memo> memos;
    private MemoManager memoManager;
    private Group group;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memos); // Make sure to create this layout file

        listViewMemos = findViewById(R.id.listViewMemos); // Add this ListView to your layout
        memos = new ArrayList<>();
        memoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewMemos.setAdapter(memoAdapter);

        memoManager = new MemoManager();

        group = getIntent().getParcelableExtra("group");
        // Get the group ID passed from the previous activity
        groupId = group.getGroupId();
        if (groupId != null) {
            loadMemosForGroup(groupId);
        } else {
            Toast.makeText(this, "Group ID is not specified", Toast.LENGTH_SHORT).show();
            finish(); // Finish activity if groupId is not found
        }
    }

    private void loadMemosForGroup(String groupId) {
        memoManager.fetchMemosByGroupId(groupId, new MemoManager.MemosCallback() {
            @Override
            public void onMemosRetrieved(List<Memo> retrievedMemos) {
                memos.clear();
                memoAdapter.clear();
                for (Memo memo : retrievedMemos) {
                    memos.add(memo);
                    memoAdapter.add(memo.getDate() + " - " + memo.getFeeling() + ": " + memo.getBodyText());
                }
                memoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ViewMemoActivity.this, "Failed to load memos: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
