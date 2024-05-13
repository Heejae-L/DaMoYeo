package android.org.firebasetest;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {
    private TextView textViewName, textViewEmail, textViewAge, textViewBio;
    private ImageView imageViewProfile;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize views
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAge = findViewById(R.id.textViewAge);
        textViewBio = findViewById(R.id.textViewBio);
        imageViewProfile = findViewById(R.id.imageViewProfile);

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
