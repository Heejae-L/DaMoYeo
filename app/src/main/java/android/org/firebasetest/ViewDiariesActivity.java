package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Diary diary = diaries.get(position);
                        Intent intent = new Intent(ViewDiariesActivity.this, DiaryActivity.class);
                        intent.putExtra("diary", diary); // Passing the Diary object
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ViewDiariesActivity.this, "Failed to load diaries.", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.writeDiary).setOnClickListener(v -> startActivity(new Intent(ViewDiariesActivity.this, WriteDiaryActivity.class)));
    }
}
