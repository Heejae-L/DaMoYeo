package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoActivity extends AppCompatActivity {
    private TextView textViewDate, textViewAuthor, textViewFeeling, textViewBodyText;
    private Button buttonEditMemo;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        userManager = new UserManager();

        textViewDate = findViewById(R.id.textViewDate);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewFeeling = findViewById(R.id.textViewFeeling);
        textViewBodyText = findViewById(R.id.textViewBodyText);
        buttonEditMemo = findViewById(R.id.buttonEditMemo);

        Memo memo = getIntent().getParcelableExtra("memo");

        if (memo != null) {
            textViewDate.setText("Date: " + memo.getDate());
            textViewFeeling.setText("Feeling: " + memo.getFeeling());
            textViewBodyText.setText("Content: " + memo.getBodyText());

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
