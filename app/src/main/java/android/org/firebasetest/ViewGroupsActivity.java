package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewGroupsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GroupManager groupManager;
    private GroupAdapter groupAdapter;
    private List<Group> groups;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        groupAdapter = new GroupAdapter(ViewGroupsActivity.this, groups);
        recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set the layout manager
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // 구분선 추가
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        groups = new ArrayList<>();
        groupAdapter = new GroupAdapter(ViewGroupsActivity.this, groups);
        recyclerView.setAdapter(groupAdapter); // Set adapter before fetching data
        groupManager = new GroupManager();


        loadUserGroups();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserGroups();  // 그룹 정보를 새로 고침
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        findViewById(R.id.addGroup).setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        findViewById(R.id.view_invitations_button).setOnClickListener(v->startActivity(new Intent(this, ViewMyInvitationsActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void loadUserGroups() {
        groupManager.fetchGroupsForUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("DataFetch", "No data found");
                    groups.clear(); // Optionally clear the list if no groups are found
                    groupAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                    return;
                }

                groups.clear(); // Clear existing group data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null) {
                        groups.add(group);
                        Log.e("loadUserGroups", "group title: " + group.getTitle());
                    }
                }
                groupAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewGroupsActivity.this, "Failed to load groups: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }


}
