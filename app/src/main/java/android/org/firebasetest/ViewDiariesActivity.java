package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ViewDiariesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryManager diaryManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DiaryAdapter adapter; // Adapter as a field to update later
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diaries);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set the layout manager
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // 구분선 추가
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        diaryManager = new DiaryManager();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 현재 사용자 ID 가져오기

        loadDiaries(); // Initial load
        setupSwipeRefreshLayout();
        setupButton();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> refreshContent());
    }

    private void setupButton() {
        findViewById(R.id.writeDiary).setOnClickListener(v -> startActivity(new Intent(ViewDiariesActivity.this, CreateDiaryActivity.class)));
        findViewById(R.id.btnSetAlarm).setOnClickListener(v -> startActivity(new Intent(ViewDiariesActivity.this, SetAlarmActivity.class)));
    }

    private void loadDiaries() {
        diaryManager.fetchDiaries(userId, new DiaryManager.DiariesCallback() {
            @Override
            public void onDiariesRetrieved(List<Diary> diaries) {
                adapter = new DiaryAdapter(ViewDiariesActivity.this, diaries);
                recyclerView.setAdapter(adapter);
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
                adapter.updateDiaries(diaries); // Assumes updateDiaries method in adapter
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
