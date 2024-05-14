package android.org.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView Name;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.d("MainActivity", "User ID: " + userId);

            UserManager userManager = new UserManager();
            userManager.fetchUserById(userId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    Name = findViewById(R.id.userName);
                    Name.setText(user.getName());
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(MainActivity.this, "Error loading user information.", Toast.LENGTH_SHORT).show();
                }
            });

            setupButtons(userId);
        } else {
            // 사용자가 로그인되어 있지 않음, 로그인 액티비티로 리디렉션
            startActivity(new Intent(this, EmailLoginActivity.class));
            finish();
        }
    }

    private void setupButtons(String userId) {
        findViewById(R.id.sign_out_button).setOnClickListener(v -> signOut());
        findViewById(R.id.createGroup).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateGroupActivity.class)));
        findViewById(R.id.viewGroups).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ViewGroupActivity.class)));
        findViewById(R.id.user_page_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        findViewById(R.id.viewDiariesButton).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ViewDiariesActivity.class)));
        findViewById(R.id.show_invitations).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewMyInvitationsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void signOut() {
        mAuth.signOut();  // Firebase에서 로그아웃
        Intent intent = new Intent(MainActivity.this, EmailLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
