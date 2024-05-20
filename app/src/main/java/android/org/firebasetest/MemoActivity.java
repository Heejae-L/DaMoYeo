package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class MemoActivity extends AppCompatActivity {
    private TextView textViewDate, textViewAuthor, textViewFeeling, textViewBodyText, textViewWeather;
    private Button buttonEditMemo;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

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

        userManager = new UserManager();

        textViewDate = findViewById(R.id.textViewDate);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewFeeling = findViewById(R.id.textViewFeeling);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewBodyText = findViewById(R.id.textViewBodyText);
        buttonEditMemo = findViewById(R.id.buttonEditMemo);

        Memo memo = getIntent().getParcelableExtra("memo");

        if (memo != null) {
            textViewDate.setText("Date: " + memo.getDate());
            textViewFeeling.setText("Feeling: " + memo.getFeeling());
            textViewBodyText.setText("Content: " + memo.getBodyText());
            textViewWeather.setText("Weather: " + memo.getWeather());

            if (memo.getAuthorId() != null) {
                userManager.fetchUserById(memo.getAuthorId(), new UserManager.UserCallback() {
                    @Override
                    public void onUserRetrieved(User user) {
                        textViewAuthor.setText("Author: " + user.getName());
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(MemoActivity.this, "Failed to fetch user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                textViewAuthor.setText("Author: Unknown");
            }
        }

        buttonEditMemo.setOnClickListener(v -> {
            Intent intent = new Intent(MemoActivity.this, EditMemoActivity.class);
            intent.putExtra("memoId", memo.getMemoId());
            startActivity(intent);
        });
    }
}
