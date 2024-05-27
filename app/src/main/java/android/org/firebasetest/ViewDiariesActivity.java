package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ViewDiariesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryManager diaryManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DiaryAdapter adapter; // Adapter as a field to update later
    private String userId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diaries);

        // Firebase 초기화
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set the layout manager
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // 구분선 추가
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
            diaryManager = new DiaryManager();

            loadDiaries(); // Initial load
            setupSwipeRefreshLayout();
            setupButton();
        } else {
            startActivity(new Intent(this, EmailLoginActivity.class));
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> refreshContent());
    }

    private void setupButton() {
        findViewById(R.id.writeDiary).setOnClickListener(v -> startActivity(new Intent(ViewDiariesActivity.this, CreateDiaryActivity.class)));
        findViewById(R.id.btnSetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDiariesActivity.this, SetAlarmActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
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
