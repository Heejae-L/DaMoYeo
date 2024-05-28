package android.org.firebasetest;

import java.util.Date;

public class ChatMessage {
    private String groupId;
    private String username;
    private String message;
    private Date timestamp;

    public ChatMessage() {
        // Default constructor required for Firestore data mapping
    }

    public ChatMessage(String groupId, String username, String message, Date timestamp, int seenCount) {
        this.groupId = groupId != null ? groupId : "";
        this.username = username != null ? username : "Unknown";
        this.message = message != null ? message : "";
        this.timestamp = timestamp != null ? timestamp : new Date();  // timestamp가 null이면 현재 시간을 기본값으로 사용

    };


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

}
