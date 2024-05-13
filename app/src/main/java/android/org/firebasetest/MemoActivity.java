package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MemoActivity extends AppCompatActivity {
    private TextView textViewDate, textViewAuthor, textViewFeeling, textViewBodyText;
    private Button buttonEditMemo;
    private String memoId;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // Initialize views
        textViewDate = findViewById(R.id.textViewDate);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewFeeling = findViewById(R.id.textViewFeeling);
        textViewBodyText = findViewById(R.id.textViewBodyText);
        buttonEditMemo = findViewById(R.id.buttonEditMemo);

        // Get the Memo object passed from the previous activity
        memo = getIntent().getParcelableExtra("memo");

        // Set the text views with memo details
        if (memo != null) {
            textViewDate.setText("Date: " + memo.getDate());
            textViewAuthor.setText("Author: " + memo.getAuthorId());
            textViewFeeling.setText("Feeling: " + memo.getFeeling());
            textViewBodyText.setText("Content: " + memo.getBodyText());
        }

        // Set click listener for the edit button
        buttonEditMemo.setOnClickListener(v -> {
            // Intent to start an EditMemoActivity (you'll need to create this)
            Intent intent = new Intent(MemoActivity.this, EditMemoActivity.class);
            intent.putExtra("memoId", memo.getMemoId()); // Passing memo ID or the whole Memo object if needed
            startActivity(intent);
        });
    }
}
