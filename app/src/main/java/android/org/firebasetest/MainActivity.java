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
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);
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
        findViewById(R.id.map_button).setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
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