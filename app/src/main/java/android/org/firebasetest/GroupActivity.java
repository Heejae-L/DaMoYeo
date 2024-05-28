package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewGroupDescription;
    private RecyclerView recyclerViewMembers;
    private DatabaseReference databaseReference;
    private List<User> memberList;
    private MemberAdapter adapter;
    private String userId;
    private User user;
    private UserManager userManager;
    private ViewMemosFragment fragment;

    private MapFragment mapFragment;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

        userManager = new UserManager();
        textViewGroupDescription = findViewById(R.id.textViewGroupDescription);

        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        memberList = new ArrayList<>();
        adapter = new MemberAdapter(this, memberList);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMembers.setAdapter(adapter);

        userId = getIntent().getStringExtra("userId");
        Log.e("why..","userId"+ userId);
        group = getIntent().getParcelableExtra("group");

        if (group != null) {
            loadGroupData();
            initializeOrRefreshFragment();
            initializeOrRefreshMapFragment(); // MapFragment 초기화 또는 새로고침
        }

        setupButtons(group);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadGroupData();
            initializeOrRefreshFragment();
            initializeOrRefreshMapFragment(); // MapFragment 새로고침
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadGroupData() {
        if (userId != null) {
            userManager.fetchUserById(userId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    GroupActivity.this.user = user;
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(GroupActivity.this, "Failed to fetch user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (group != null) {
            textViewGroupDescription.setText(group.getDescription());
            Toolbar toolbar = findViewById(R.id.top_app_bar);
            toolbar.setTitle(group.getTitle());
            displayGroupMembers(group.getGroupId());
        }
    }

    private void displayGroupMembers(String groupId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("memberIds");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("users").child(userId).child("name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.getValue(String.class);
                                    User member = new User(userId, userName, "", 0, "", "", "");
                                    if (userName != null) {
                                        memberList.add(member);
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(GroupActivity.this, "Failed to load user names.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroupActivity.this, "Failed to load group members.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeOrRefreshFragment() {
        if (fragment == null) {
            fragment = new ViewMemosFragment();
        }
        Bundle args = new Bundle();
        args.putString("groupId", group.getGroupId());
        fragment.setArguments(args);

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void initializeOrRefreshMapFragment() {
        if (mapFragment == null) {
            mapFragment = new MapFragment();
        }
        Bundle args = new Bundle();
        args.putString("groupId", group.getGroupId());
        mapFragment.setArguments(args);

        if (!mapFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_map_container, mapFragment) // R.id.fragment_map_container로 mapFragment 추가
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_map_container, mapFragment) // R.id.fragment_map_container로 mapFragment 교체
                    .commit();
        }
    }

    private void setupButtons(Group group) {
        Button writeMemo = findViewById(R.id.WriteMemoButton);
        writeMemo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WriteMemoActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        Button viewMemos = findViewById(R.id.ViewMemoButton);
        viewMemos.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewMemosActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        Button groupChatButton = findViewById(R.id.GroupChatButton);
        groupChatButton.setOnClickListener(v -> startChatActivity(group, userId));

        Button addMemberButton = findViewById(R.id.AddMemberButton);
        addMemberButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, AddGroupMemberActivity.class);
            intent.putExtra("groupId", group.getGroupId());
            intent.putExtra("userId", userId);
            Log.e("why..","userId"+ userId);
            startActivity(intent);
        });
        Button ViewMapButton = findViewById(R.id.ViewMapButton);
        ViewMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, MapActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        findViewById(R.id.view_chat_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, RealtimeChat.class);
            intent.putExtra("groupId", group.getGroupId());
            intent.putExtra("userId", userId);
            Log.e("why..","userId"+ userId);
            startActivity(intent);});
    }

    private void startChatActivity(Group group, String userId) {
        Intent intent = new Intent(this, RealtimeChat.class);
        intent.putExtra("groupId", group.getGroupId());
        intent.putExtra("userId", userId);
        Log.e("why..","userId"+ userId);
        startActivity(intent);
    }
}
