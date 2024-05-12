package android.org.firebasetest;

public class Calendar {
    private String calendarId;
    private boolean isPrivate;
    private String memo;
    private String startDate;
    private String endDate;
    private String title;
    private String userId;

    public Calendar() {
        // Default constructor required for Firebase
    }

    public Calendar(String calendarId, boolean isPrivate, String memo, String startDate, String endDate, String title, String userId) {
        this.calendarId = calendarId;
        this.isPrivate = isPrivate;
        this.memo = memo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.userId = userId;
    }

    // Getters and setters
    public String getCalendarId() { return calendarId; }
    public void setCalendarId(String calendarId) { this.calendarId = calendarId; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
