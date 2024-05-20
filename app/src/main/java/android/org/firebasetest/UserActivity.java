package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewName, textViewEmail, textViewAge, textViewBio;
    private ImageView imageViewProfile;
    private DatabaseReference databaseReference;
    private String userId;
    ProfileImageManager imageManager;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call method to load user data
                loadUserData();
                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing indicator
            }
        });

        // Initialize views
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAge = findViewById(R.id.textViewAge);
        textViewBio = findViewById(R.id.textViewBio);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        imageManager = new ProfileImageManager();

        // Get user ID from intent
        userId = getIntent().getStringExtra("userId");
        Log.d("UserActivity",userId);
        if (userId == null) {
            Toast.makeText(this, "User ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to user's data in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        loadUserData();

        editProfileButton = findViewById(R.id.buttonEditProfile);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, EditUserActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }

    private void loadUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    textViewName.setText(user.getName());
                    textViewEmail.setText(user.getEmail());
                    textViewAge.setText(String.valueOf(user.getAge()));
                    textViewBio.setText(user.getBio());
                    imageManager.loadProfileImage(UserActivity.this, imageViewProfile, userId);

                } else {
                    Toast.makeText(UserActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserActivity.this, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
