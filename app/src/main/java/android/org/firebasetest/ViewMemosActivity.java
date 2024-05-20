package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ViewMemosActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listViewMemos;
    private Button writeMemoButton, deleteMemoButton;
    private ArrayAdapter<String> memoAdapter;
    private List<Memo> memos;
    private MemoManager memoManager;
    private Group group;
    private String groupId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memos);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listViewMemos = findViewById(R.id.listViewMemos);
        memos = new ArrayList<>();
        memoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewMemos.setAdapter(memoAdapter);

        memoManager = new MemoManager();
        group = getIntent().getParcelableExtra("group");
        userId = getIntent().getStringExtra("userId");

        if (group != null) {
            groupId = group.getGroupId();
            refreshMemos();
        } else {
            Toast.makeText(this, "Group ID is not specified", Toast.LENGTH_SHORT).show();
            finish();
        }

        listViewMemos.setOnItemClickListener((parent, view, position, id) -> {
            Memo selectedMemo = memos.get(position);
            Intent intent = new Intent(ViewMemosActivity.this, MemoActivity.class);
            intent.putExtra("memo", selectedMemo); // Passing the selected Memo object to MemoActivity
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshMemos);

        writeMemoButton = findViewById(R.id.writeMemoButton);
        writeMemoButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WriteMemoActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        deleteMemoButton = findViewById(R.id.deleteMemoButton);
        // Setup delete button if necessary

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);
    }

    private void refreshMemos() {
        if (groupId != null) {
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
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(ViewMemosActivity.this, "Failed to load memos: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            Toast.makeText(this, "Group ID is not specified", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
