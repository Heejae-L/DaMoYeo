package android.org.firebasetest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteMemoActivity extends AppCompatActivity {
    private EditText editTextFeeling, editTextBodyText;
    private Button buttonSaveMemo;
    private MemoManager memoManager;
    private Group group;
    private String groupId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memo);

        // Initialize views and Firebase components
        editTextFeeling = findViewById(R.id.editTextFeeling);
        editTextBodyText = findViewById(R.id.editTextBodyText);
        buttonSaveMemo = findViewById(R.id.buttonSaveMemo);
        memoManager = new MemoManager();

        // Get current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            // Handle the case where the user is not logged in
        }
        group = getIntent().getParcelableExtra("group");
        // Get the group ID passed from the previous activity
        groupId = group.getGroupId();

        // Set click listener for the save memo button
        buttonSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemo();
            }
        });
    }

    private void saveMemo() {
        String feeling = editTextFeeling.getText().toString().trim();
        String bodyText = editTextBodyText.getText().toString().trim();
        String memoId = memoManager.getDatabase().push().getKey(); // Generate unique ID for the memo

        if (feeling.isEmpty() || bodyText.isEmpty()) {
            Toast.makeText(WriteMemoActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Memo object with the provided data
        Memo memo = new Memo(memoId, feeling, getCurrentDate(), bodyText, null, userId, groupId);
        Log.d("Memo","Memo:"+memo.getGroupId()+groupId);

        // Save the memo to the database
        memoManager.saveMemo(memo);

        // Show a success message
        Toast.makeText(WriteMemoActivity.this, "Memo saved successfully", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
