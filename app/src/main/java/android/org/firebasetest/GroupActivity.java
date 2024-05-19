package android.org.firebasetest;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    private TextView textViewGroupTitle, textViewGroupDescription;
    private ListView listViewMembers;
    private DatabaseReference databaseReference;
    private ArrayList<String> memberNames;
    private ArrayAdapter<String> adapter;
    String userId;
    User user;
    UserManager userManager;
    ViewMemosFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

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

        userManager = new UserManager();
        textViewGroupTitle = findViewById(R.id.textViewGroupTitle);
        textViewGroupDescription = findViewById(R.id.textViewGroupDescription);
        listViewMembers = findViewById(R.id.listViewMembers);
        memberNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberNames);
        listViewMembers.setAdapter(adapter);

        Group group = getIntent().getParcelableExtra("group");

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            userManager.fetchUserById(userId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    GroupActivity.this.user = user; // Now you have the user, and you can use it in your activity
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(GroupActivity.this, "Failed to fetch user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (group != null) {
            textViewGroupTitle.setText(group.getTitle());
            textViewGroupDescription.setText(group.getDescription());
            displayGroupMembers(group.getGroupId());
        }

        fragment = new ViewMemosFragment();
        Bundle args = new Bundle();
        args.putString("groupId", group.getGroupId());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();

        setupButtons(group);
    }

    private void displayGroupMembers(String groupId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("memberIds");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("users").child(userId).child("name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.getValue(String.class);
                                    if (userName != null) {
                                        memberNames.add(userName);
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

    private void startChatActivity(Group group, String userId) {
        // 채팅 액티비티를 시작하는 로직을 구현합니다.
        Intent intent = new Intent(this, RealtimeChat.class);
        intent.putExtra("groupId", group.getGroupId());
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void setupButtons(Group group) {
        Button writeMemo = findViewById(R.id.WriteMemoButton);
        writeMemo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WriteMemoActivity.class);
            intent.putExtra("group", group); // Passing the Group object
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        Button viewMemos = findViewById(R.id.ViewMemoButton);
        viewMemos.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewMemosActivity.class);
            intent.putExtra("group", group); // Passing the Group object
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        Button groupChatButton = findViewById(R.id.GroupChatButton);
        groupChatButton.setOnClickListener(v -> {
            startChatActivity(group, userId);
        });

        Button addMemberButton = findViewById(R.id.AddMemberButton);
        addMemberButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, AddGroupMemberActivity.class);
            intent.putExtra("groupId", group.getGroupId());
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}
