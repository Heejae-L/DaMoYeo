package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ViewDiariesActivity extends AppCompatActivity {

    private ListView listView;
    private DiaryManager diaryManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<Diary> adapter; // Adapter as a field to update later
    private List<Diary> diaryList; // Store diaries list as a field
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diaries);

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


        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        diaryManager = new DiaryManager();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 현재 사용자 ID 가져오기

        setupDiaryListView();
        setupSwipeRefreshLayout();
        setupWriteDiaryButton();
    }

    private void setupDiaryListView() {
        loadDiaries(); // Initial load
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Diary diary = diaryList.get(position);
                Intent intent = new Intent(ViewDiariesActivity.this, DiaryActivity.class);
                intent.putExtra("diary", diary); // Passing the Diary object
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void setupWriteDiaryButton() {
        findViewById(R.id.writeDiary).setOnClickListener(v -> startActivity(new Intent(ViewDiariesActivity.this, WriteDiaryActivity.class)));
    }

    private void loadDiaries() {
        diaryManager.fetchDiaries(userId, new DiaryManager.DiariesCallback() {
            @Override
            public void onDiariesRetrieved(List<Diary> diaries) {
                diaryList = diaries; // Save diaries
                adapter = new DiaryAdapter(ViewDiariesActivity.this, R.layout.diary_list_item, diaries);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ViewDiariesActivity.this, "Failed to load diaries.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshContent() {
        diaryManager.fetchDiaries(userId, new DiaryManager.DiariesCallback() {
            @Override
            public void onDiariesRetrieved(List<Diary> diaries) {
                diaryList = diaries; // Update diaries
                adapter.clear();
                adapter.addAll(diaries);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing indicator
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ViewDiariesActivity.this, "Failed to refresh diaries.", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
