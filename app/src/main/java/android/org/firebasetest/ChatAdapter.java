package android.org.firebasetest;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private Context context;
    private ArrayList<ChatMessage> messages;
    private String myUsername;


    public ChatAdapter(Context context, ArrayList<ChatMessage> messages, String myUsername) {
        this.context = context;
        this.messages = messages;
        this.myUsername = myUsername;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        try {
            ChatMessage message = messages.get(position);
            holder.username.setText(message.getUsername());
            holder.message.setText(message.getMessage());
            holder.time.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.getTimestamp()));

            // 모든 뷰에 기본적으로 흰색 배경을 설정
            holder.itemView.setBackgroundColor(Color.WHITE);

            // 현재 사용자의 메시지인 경우 노란색으로 변경
            if (message.getUsername().equalsIgnoreCase(myUsername.trim())) {
                holder.itemView.setBackgroundColor(Color.YELLOW);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE); // 재활용된 뷰의 배경색을 초기화
            }


        } catch (IndexOutOfBoundsException e) {
            Log.e("ChatAdapter", "IndexOutOfBoundsException at position " + position);
            Toast.makeText(context.getApplicationContext(), "IndexOutOfBoundsException at position " + position, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chat_name);
            message = itemView.findViewById(R.id.chat_contents);
            time = itemView.findViewById(R.id.chat_time);
        }
    }
}
