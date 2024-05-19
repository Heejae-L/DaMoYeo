package android.org.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView name;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 초기화
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.userName);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.d("MainActivity", "User ID: " + userId);

            UserManager userManager = new UserManager();
            userManager.fetchUserById(userId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    name.setText(user.getName());
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(MainActivity.this, "Error loading user information.", Toast.LENGTH_SHORT).show();
                }
            });

            setupButtons(userId);
        } else {
            startActivity(new Intent(this, EmailLoginActivity.class));
            finish();
        }

        // 바텀 네비게이션 뷰 초기화 및 이벤트 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            } else if (item.getItemId() == R.id.navigation_shared) {
                startActivity(new Intent(this, ViewGroupsActivity.class));
                return true;

            } else if (item.getItemId() == R.id.navigation_calendar) {
                // 캘린더 보기 기능을 구현
                return true;

            } else if (item.getItemId() == R.id.navigation_profile) {
                Intent intent = new Intent(this, UserActivity.class);
                intent.putExtra("userId", mAuth.getCurrentUser().getUid());
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupButtons(String userId) {
        findViewById(R.id.sign_out_button).setOnClickListener(v -> signOut());
        findViewById(R.id.createGroup).setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        findViewById(R.id.viewGroups).setOnClickListener(v -> startActivity(new Intent(this, ViewGroupsActivity.class)));
        findViewById(R.id.user_page_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        findViewById(R.id.viewDiariesButton).setOnClickListener(v -> startActivity(new Intent(this, ViewDiariesActivity.class)));
        findViewById(R.id.show_invitations).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewMyInvitationsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void signOut() {
        mAuth.signOut(); // Firebase에서 로그아웃
        Intent intent = new Intent(this, EmailLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
