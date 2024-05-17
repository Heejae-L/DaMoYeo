package android.org.firebasetest;

import java.util.Date;

public class ChatMessage {
    private String groupId;
    private String username;
    private String message;
    private Date timestamp;
    private int seenCount; // Count of members who have seen the message

    public ChatMessage() {
        // Default constructor required for Firestore data mapping
    }

    public ChatMessage(String groupId, String username, String message, Date timestamp, int seenCount) {
        this.groupId = groupId;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.seenCount = seenCount;
    }

    // Getters and setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getSeenCount() {
        return seenCount;
    }

    public void setSeenCount(int seenCount) {
        this.seenCount = seenCount;
    }
}
