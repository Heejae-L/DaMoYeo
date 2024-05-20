package android.org.firebasetest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationHelper {

    public static void setupToolbar(Toolbar toolbar, Context context) {
        toolbar.setNavigationOnClickListener(v -> ((Activity) context).finish());
    }

    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, Context context) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                context.startActivity(new Intent(context, MainActivity.class));
                ((Activity) context).finish();
                return true;
            } else if (itemId == R.id.navigation_shared) {
                context.startActivity(new Intent(context, ViewGroupsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                context.startActivity(new Intent(context, CalendarActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("userId", mAuth.getCurrentUser().getUid());
                context.startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
