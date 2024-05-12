package android.org.firebasetest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.stream.Collectors;

public class ViewDiariesActivity extends AppCompatActivity {

    private ListView listView;
    private DiaryManager diaryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diaries);

        listView = findViewById(R.id.listView);
        diaryManager = new DiaryManager();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 현재 사용자 ID 가져오기
        diaryManager.fetchDiaries(userId, new DiaryManager.DiariesCallback() {
            @Override
            public void onDiariesRetrieved(List<Diary> diaries) {
                // ListView에 다이어리 표시
                ArrayAdapter<Diary> adapter = new DiaryAdapter(ViewDiariesActivity.this, R.layout.diary_list_item, diaries);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ViewDiariesActivity.this, "Failed to load diaries.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
