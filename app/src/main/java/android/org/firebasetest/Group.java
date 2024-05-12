package android.org.firebasetest;

import java.util.List;

public class Group {
    private String groupId;
    private String description;
    private String title;
    private String date;
    private List<String> memberIds;

    public Group() {
        // Default constructor required for Firebase
    }

    public Group(String groupId, String description, String title, String date, List<String> memberIds) {
        this.groupId = groupId;
        this.description = description;
        this.title = title;
        this.date = date;
        this.memberIds = memberIds;
    }

    // Getters and setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }
}
