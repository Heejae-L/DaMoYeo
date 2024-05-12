package android.org.firebasetest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GroupActivity extends AppCompatActivity {
    private TextView textViewGroupTitle, textViewGroupDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        textViewGroupTitle = findViewById(R.id.textViewGroupTitle);
        textViewGroupDescription = findViewById(R.id.textViewGroupDescription);

        Group group = getIntent().getParcelableExtra("group");
        if (group != null) {
            textViewGroupTitle.setText(group.getTitle());
            textViewGroupDescription.setText(group.getDescription());
        }

        Button writeMemo = findViewById(R.id.WriteMemoButton);
        writeMemo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WriteMemoActivity.class);
            intent.putExtra("group", group); // Passing the Group object
            startActivity(intent);
        });

        Button viewMemo = findViewById(R.id.ViewMemoButton);
        viewMemo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ViewMemoActivity.class);
            intent.putExtra("group", group); // Passing the Group object
            startActivity(intent);
        });

    }

}
