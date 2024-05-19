package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ViewMemosActivity extends AppCompatActivity {
    private ListView listViewMemos;
    private Button writeMemoButton, DeleteMemoButton;
    private ArrayAdapter<String> memoAdapter;
    private List<Memo> memos;
    private MemoManager memoManager;
    private Group group;
    private String groupId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memos); // Make sure to create this layout file

        // 뒤로가기
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);  // Toolbar를 액티비티의 앱 바로 설정합니다.

        // 뒤로가기 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로가기 버튼이 클릭되면 현재 액티비티를 종료합니다.
                finish();
            }
        });

        listViewMemos = findViewById(R.id.listViewMemos); // Add this ListView to your layout
        memos = new ArrayList<>();
        memoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewMemos.setAdapter(memoAdapter);

        memoManager = new MemoManager();

        group = getIntent().getParcelableExtra("group");
        userId = getIntent().getStringExtra("userId");

        // Get the group ID passed from the previous activity
        groupId = group.getGroupId();
        if (groupId != null) {
            loadMemosForGroup(groupId);
        } else {
            Toast.makeText(this, "Group ID is not specified", Toast.LENGTH_SHORT).show();
            finish(); // Finish activity if groupId is not found
        }
        listViewMemos.setOnItemClickListener((parent, view, position, id) -> {
            Memo selectedMemo = memos.get(position);
            Intent intent = new Intent(ViewMemosActivity.this, MemoActivity.class);
            intent.putExtra("memo", selectedMemo); // Passing the selected Memo object to MemoActivity
            startActivity(intent);
        });

        writeMemoButton = findViewById(R.id.writeMemoButton);
        writeMemoButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WriteMemoActivity.class);
            intent.putExtra("group", group); // Passing the Group object
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

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
                Toast.makeText(ViewMemosActivity.this, "Failed to load memos: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}