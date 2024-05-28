package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;

public class MemoActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewDate, textViewAuthor, textViewFeeling, textViewBodyText, textViewWeather;
    private Button buttonEditMemo;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        userManager = new UserManager();

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDate = findViewById(R.id.textViewDate);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewFeeling = findViewById(R.id.textViewFeeling);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewBodyText = findViewById(R.id.textViewBodyText);
        buttonEditMemo = findViewById(R.id.buttonEditMemo);

        Memo memo = getIntent().getParcelableExtra("memo");
        Log.d("MemoActivity","memo: " + memo);

        if (memo != null) {
            textViewTitle.setText(memo.getTitle());
            textViewDate.setText("Date: " + memo.getDate());
            textViewFeeling.setText("Feeling: " + memo.getFeeling());
            textViewBodyText.setText(memo.getBodyText());
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
            intent.putExtra("memo",memo);
            startActivity(intent);
        });
    }
}
