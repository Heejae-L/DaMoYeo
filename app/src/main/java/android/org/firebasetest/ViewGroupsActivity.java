package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
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
    private ListView listViewGroups;
    private Button createGroupButton, viewInvitationsButton;
    private GroupManager groupManager;
    private ArrayAdapter<String> adapter;
    private List<Group> groups;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        // 이 부분을 추가합니다.
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);  // Toolbar를 액티비티의 앱 바로 설정합니다.

        // 뒤로가기 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로가기 버튼이 클릭되면 현재 액티비티를 종료합니다.
                finish();
            }
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listViewGroups = findViewById(R.id.listViewGroups);
        groups = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewGroups.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        groupManager = new GroupManager();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserGroups();  // 그룹 정보를 새로 고침
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadUserGroups();

        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groups.get(position);
                Intent intent = new Intent(ViewGroupsActivity.this, GroupActivity.class);
                intent.putExtra("group", group); // Passing the Group object
                intent.putExtra("user", user);
                intent.putExtra("userId", user.getUid());
                startActivity(intent);
            }
        });


        findViewById(R.id.addGroup).setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));

        findViewById(R.id.view_invitations_button).setOnClickListener(v->startActivity(new Intent(this, ViewMyInvitationsActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigationView(bottomNavigationView, this);

    }

    private void loadUserGroups() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ViewGroupActivity", "Loading groups for user ID: " + userId); // 사용자 ID 로그

        groupManager.fetchGroupsForUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups.clear();
                adapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null) {
                        groups.add(group);
                        adapter.add(group.getTitle() + " - " + group.getDescription());
                        Log.d("ViewGroupActivity", "Group loaded: " + group.getTitle() + " - " + group.getDescription()); // 각 그룹 로드 로그

                        // 멤버 ID 리스트 로그
                        Map<String, Boolean> memberIds = group.getMemberIds();
                        if (memberIds != null && !memberIds.isEmpty()) {
                            Log.d("ViewGroupActivity", "Members of " + group.getTitle() + ": " + memberIds.keySet().toString());
                        } else {
                            Log.d("ViewGroupActivity", "No members found in " + group.getTitle());
                        }

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewGroupsActivity.this, "Failed to load groups: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ViewGroupActivity", "Failed to load groups: " + databaseError.getMessage()); // 로드 실패 로그
            }
        });
    }
}
