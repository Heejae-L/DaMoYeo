package android.org.firebasetest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        ChatMessage message = messages.get(position);
        holder.username.setText(message.getUsername());
        holder.message.setText(message.getMessage());
        holder.time.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.getTimestamp()));

        //내가 작성한건 노란색, 남이 작성한 채팅은 흰색으로
        if (message.getUsername().equals(myUsername)) {
            holder.itemView.setBackgroundColor(Color.YELLOW); // Highlight own messages
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE); // Normal for others
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
