package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.DateTime;

public class EventDetail implements Parcelable {
    private String summary;
    private DateTime startTime; // DateTime으로 저장
    private DateTime endTime; // DateTime으로 저장
    private String eventId;
    private String location; // 위치를 추가합니다.
    private String description; // 설명을 추가합니다.
    private String formattedStartTime;
    private String formattedEndTime;

    // 기존 생성자
    public EventDetail(String summary, DateTime startTime, DateTime endTime, String eventId, String location, String description, String formattedStartTime, String formattedEndTime) {
        this.summary = summary;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventId = eventId;
        this.location = location;
        this.description = description;
        this.formattedStartTime = formattedStartTime;
        this.formattedEndTime = formattedEndTime;
    }

    // 새로운 간단한 생성자
    public EventDetail(String summary, DateTime startTime, String eventId) {
        this.summary = summary;
        this.startTime = startTime;
        this.eventId = eventId;
    }

    protected EventDetail(Parcel in) {
        summary = in.readString();
        long startTimeLong = in.readLong();
        long endTimeLong = in.readLong();
        startTime = startTimeLong == -1 ? null : new DateTime(startTimeLong);
        endTime = endTimeLong == -1 ? null : new DateTime(endTimeLong);
        eventId = in.readString();
        location = in.readString();
        description = in.readString();
        formattedStartTime = in.readString();
        formattedEndTime = in.readString();
    }

    public static final Creator<EventDetail> CREATOR = new Creator<EventDetail>() {
        @Override
        public EventDetail createFromParcel(Parcel in) {
            return new EventDetail(in);
        }

        @Override
        public EventDetail[] newArray(int size) {
            return new EventDetail[size];
        }
    };

    public String getSummary() {
        return summary;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public String getFormattedEndTime() {
        return formattedEndTime;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormattedStartTime(String formattedStartTime) {
        this.formattedStartTime = formattedStartTime;
    }

    public void setFormattedEndTime(String formattedEndTime) {
        this.formattedEndTime = formattedEndTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(summary);
        dest.writeLong(startTime != null ? startTime.getValue() : -1);
        dest.writeLong(endTime != null ? endTime.getValue() : -1);
        dest.writeString(eventId);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(formattedStartTime);
        dest.writeString(formattedEndTime);
    }
}
