package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewGroupActivity extends AppCompatActivity {
    private ListView listViewGroups;
    private GroupManager groupManager;
    private ArrayAdapter<String> adapter;
    private List<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        listViewGroups = findViewById(R.id.listViewGroups);
        groups = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewGroups.setAdapter(adapter);

        groupManager = new GroupManager();
        loadUserGroups();

        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groups.get(position);
                Intent intent = new Intent(ViewGroupActivity.this, GroupActivity.class);
                intent.putExtra("group", group); // Passing the Group object
                startActivity(intent);
            }
        });

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
                Toast.makeText(ViewGroupActivity.this, "Failed to load groups: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ViewGroupActivity", "Failed to load groups: " + databaseError.getMessage()); // 로드 실패 로그
            }
        });
    }
}