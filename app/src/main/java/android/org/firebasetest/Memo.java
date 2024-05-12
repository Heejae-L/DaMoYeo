package android.org.firebasetest;

public class Memo {
    private String memoId;
    private String feeling;
    private String date;
    private String bodyText;
    private String image;
    private String authorId;

    public Memo() {
        // Default constructor required for Firebase
    }

    public Memo(String memoId, String feeling, String date, String bodyText, String image, String authorId) {
        this.memoId = memoId;
        this.feeling = feeling;
        this.date = date;
        this.bodyText = bodyText;
        this.image = image;
        this.authorId = authorId;
    }

    // Getters and setters
    public String getMemoId() { return memoId; }
    public void setMemoId(String memoId) { this.memoId = memoId; }
    public String getFeeling() { return feeling; }
    public void setFeeling(String feeling) { this.feeling = feeling; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
}
