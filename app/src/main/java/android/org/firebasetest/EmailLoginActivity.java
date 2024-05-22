package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailLoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button createAccountButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        FirebaseUser user = mAuth.getCurrentUser();  // 현재 로그인한 사용자 가져오기

                        if (user != null) {
                            if (user.isEmailVerified()||!user.isEmailVerified()) {
                                // 이메일 인증이 완료된 경우
                                Toast.makeText(EmailLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                String userId = user.getUid();  // 사용자 ID 가져오기
                                Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);  // 인텐트에 사용자 ID 추가
                                startActivity(intent);  // MainActivity 시작
                                finish();  // 현재 액티비티 종료
                            } else {
                                // 이메일 인증이 완료되지 않은 경우
                                Toast.makeText(EmailLoginActivity.this, "Please verify your email address.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();  // 이메일 인증이 완료되지 않으면 로그아웃
                            }
                        }
                    } else {
                        // 로그인 실패
                        Toast.makeText(EmailLoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void createUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmailLoginActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        // Optionally sign the user in or update UI
                    } else {
                        Toast.makeText(EmailLoginActivity.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
