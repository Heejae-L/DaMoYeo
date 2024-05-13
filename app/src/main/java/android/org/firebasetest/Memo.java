package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

public class Memo implements Parcelable {
    private String memoId;
    private String feeling;
    private String date;
    private String bodyText;
    private String image;
    private String authorId;
    private String groupId;

    // Default constructor
    public Memo() {
    }

    public Memo(String memoId, String feeling, String date, String bodyText, String image, String authorId, String groupId) {
        this.memoId = memoId;
        this.feeling = feeling;
        this.date = date;
        this.bodyText = bodyText;
        this.image = image;
        this.authorId = authorId;
        this.groupId = groupId;
    }

    protected Memo(Parcel in) {
        memoId = in.readString();
        feeling = in.readString();
        date = in.readString();
        bodyText = in.readString();
        image = in.readString();
        authorId = in.readString();
        groupId = in.readString();
    }

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel in) {
            return new Memo(in);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(memoId);
        dest.writeString(feeling);
        dest.writeString(date);
        dest.writeString(bodyText);
        dest.writeString(image);
        dest.writeString(authorId);
        dest.writeString(groupId);
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
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}
