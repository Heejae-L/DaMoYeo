package android.org.firebasetest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import androidx.recyclerview.widget.DiffUtil;
import androidx.appcompat.widget.Toolbar;

public class RealtimeChat extends AppCompatActivity {

    private FirebaseFirestore db;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    private EditText editTextMessage;
    private String myUsername;
    private String groupId;
    private User currentUser;
    private String myUserId;
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_chat);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        db = FirebaseFirestore.getInstance();
        userManager = new UserManager();
        myUserId = getIntent().getStringExtra("userId");
        Log.e("chat","userId=" + myUserId);
        groupId = getIntent().getStringExtra("groupId");
        Log.e("chat","Group=" + groupId);

        if (myUserId != null) {
            userManager.fetchUserById(myUserId, new UserManager.UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    currentUser = user;
                    myUsername = currentUser.getName();
                    initializeAdapter();
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(RealtimeChat.this, "Failed to fetch user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    myUsername = "unknown";
                    initializeAdapter();
                }
            });
        } else {
            myUsername = "unknown";
            initializeAdapter();

        }

        Button buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);
        // RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);

        //adapter = new ChatAdapter(this, chatList, myUsername);
        //chatRecyclerView.setAdapter(adapter);
        //chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
            }
        });

        loadMessages();
    }

    private void initializeAdapter() {
        RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);
        adapter = new ChatAdapter(this, chatList, myUsername);
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void loadMessages() {
        CollectionReference messagesRef = db.collection("Chat").document(groupId).collection("Messages");
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                return; // Handle the error
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                ArrayList<ChatMessage> newMessages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    ChatMessage message = doc.toObject(ChatMessage.class);
                    if (message.getTimestamp() == null) {
                        message.setTimestamp(new Date());
                    }

                    if (message.getMessage() != null) {  // 추가된 null 체크
                        newMessages.add(message);
                    }

                }
                updateChatList(newMessages);
            }
        });
    }

    private void updateChatList(ArrayList<ChatMessage> newMessages) {
        if (adapter != null) { // adapter가 null이 아닌지 확인
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChatDiffCallback(chatList, newMessages));
            chatList.clear();
            chatList.addAll(newMessages);
            diffResult.dispatchUpdatesTo(adapter);
            scrollToBottom();
        }
    }
    private void sendMessage(String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("username", myUsername);
        msg.put("message", text);
        msg.put("timestamp", new Date());

        db.collection("Chat").document(groupId).collection("Messages")
                .add(msg)
                .addOnSuccessListener(documentReference -> scrollToBottom())
                .addOnFailureListener(e -> Toast.makeText(RealtimeChat.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());



    }



    private void scrollToBottom() {
        RecyclerView recyclerView = findViewById(R.id.chatRecyclerView);
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (itemCount > 0) {
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }
        }
    }

}
