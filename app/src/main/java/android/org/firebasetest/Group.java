package android.org.firebasetest;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Group implements Parcelable {
    private String groupId;
    private String description;
    private String title;
    private String date;
    private String latitude;
    private String longitude;
    private Map<String, Boolean> memberIds; // memberIds as a Map

    public Group() {
        // Default constructor required for Firebase
    }

    public Group(String groupId, String description, String title, String date, Map<String, Boolean> memberIds, String latitude, String longitude) {
        this.groupId = groupId;
        this.description = description;
        this.title = title;
        this.date = date;
        this.memberIds = memberIds;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Group(Parcel in) {
        groupId = in.readString();
        description = in.readString();
        title = in.readString();
        date = in.readString();
        int size = in.readInt();
        memberIds = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            Boolean value = (in.readInt() == 1);
            memberIds.put(key, value);
        }
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeInt(memberIds.size()); // Write the size of the map
        for (Map.Entry<String, Boolean> entry : memberIds.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue() ? 1 : 0); // Write the Boolean as an int
        }
        dest.writeString(latitude);
        dest.writeString(longitude);
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
    public Map<String, Boolean> getMemberIds() { return memberIds; }
    public void setMemberIds(Map<String, Boolean> memberIds) { this.memberIds = memberIds; }
    public String getLatitude(){return latitude;}
    public void setLatitude(String latitude){this.latitude = latitude;}

    public String getLongitude(){return longitude;}
    public void setLongitude(String longitude){this.longitude = longitude;}
}
