package android.org.firebasetest;

public class Notification {
    private String notificationId;
    private String authorId;
    private String content;
    private boolean vibrate;
    private String timestamp;

    public Notification() {
        // Default constructor required for Firebase
    }

    public Notification(String notificationId, String authorId, String content, boolean vibrate, String timestamp) {
        this.notificationId = notificationId;
        this.authorId = authorId;
        this.content = content;
        this.vibrate = vibrate;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isVibrate() { return vibrate; }
    public void setVibrate(boolean vibrate) { this.vibrate = vibrate; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
