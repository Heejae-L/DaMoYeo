package android.org.firebasetest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RealtimeChat extends AppCompatActivity {

    private FirebaseFirestore db;
    private ChatAdapter adapter;
    private final ArrayList<ChatMessage> chatList = new ArrayList<>();
    private EditText editTextMessage;
    private String myUsername;
    private String groupId; // 사용자 이름을 설정합니다.
    private User currentUser;
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_chat);

        // 뒤로가기
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

        db = FirebaseFirestore.getInstance();
        userManager = new UserManager();

        String myUserId = getIntent().getStringExtra("userId");
        groupId = getIntent().getStringExtra("groupId");
        if (myUserId != null) {
            userManager.fetchUserById(myUserId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(android.org.firebasetest.User user) {
                    RealtimeChat.this.currentUser = user;
                    myUsername = currentUser.getName();
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(RealtimeChat.this, "Failed to fetch user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            myUsername = "unknown";
        }


        Button buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);
        RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);

        adapter = new ChatAdapter(this, chatList, myUsername);
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                    editTextMessage.setText("");
                }
            }
        });

        loadMessages();

    }

    private void loadMessages() {
        CollectionReference messagesRef = db.collection("Chat").document(groupId).collection("Messages");
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return; // 오류가 있는 경우 이벤트 처리를 중지
                }

                if (!snapshots.isEmpty()) {
                    ArrayList<ChatMessage> newMessages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        ChatMessage message = doc.toObject(ChatMessage.class);
                        newMessages.add(message);
                    }

                    // 변경 사항을 감지하고 적절한 알림을 발생시키기
                    updateChatList(newMessages);
                }
            }
        });
    }

    private void updateChatList(ArrayList<ChatMessage> newMessages) {
        // 새 메시지가 기존의 것과 다르면 업데이트
        if (!newMessages.equals(chatList)) {
            chatList.clear();
            chatList.addAll(newMessages);
            adapter.notifyDataSetChanged(); // 전체 데이터가 변경되었음을 알림
        }
        scrollToBottom(); // 스크롤을 가장 아래로 이동
    }




    //cloud store에 저장하는 함수
    //Chat>groupId>Message로 저장됨 Message의 구성은 ChatMessage
    //groupId로 동적으로 운영가능
    private void sendMessage(String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("username", myUsername);
        msg.put("message", text);
        msg.put("timestamp", new Date());
        msg.put("seenCount", getGroupMemberCount() - 1); // 그룹 멤버 수를 설정해야 합니다.

        db.collection("Chat").document(groupId).collection("Messages")
                .add(msg)
                .addOnSuccessListener(documentReference -> {
                    adapter.notifyItemInserted(chatList.size() - 1);
                    scrollToBottom();
                })
                .addOnFailureListener(e -> {
                    // 에러 핸들링
                });
    }

    //채팅 스크롤 시점을 제일 최근에 맞춤
    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            ((RecyclerView) findViewById(R.id.chatRecyclerView)).smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    //
    private int getGroupMemberCount() {
        // 실제 구현에서는 Firestore에서 그룹 멤버 수를 불러와야 합니다.
        return 5; // 임시적으로 멤버 수를 5로 설정
    }
}
