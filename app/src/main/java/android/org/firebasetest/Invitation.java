package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

public class Invitation implements Parcelable {
    private String invitationId;
    private String groupId;
    private String inviterId;
    private String inviteeId;
    private String date;
    private boolean accepted;
    private String status;  // 추가된 상태 필드

    // Default constructor for Firebase
    public Invitation() {
    }

    public Invitation(String invitationId, String groupId, String inviterId, String inviteeId, String date, boolean accepted, String status) {
        this.invitationId = invitationId;
        this.groupId = groupId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.date = date;
        this.accepted = accepted;
        this.status = status;  // 상태 초기화
    }

    protected Invitation(Parcel in) {
        invitationId = in.readString();
        groupId = in.readString();
        inviterId = in.readString();
        inviteeId = in.readString();
        date = in.readString();
        accepted = in.readByte() != 0;
        status = in.readString();  // Parcelable로부터 상태 읽기
    }

    public static final Creator<Invitation> CREATOR = new Creator<Invitation>() {
        @Override
        public Invitation createFromParcel(Parcel in) {
            return new Invitation(in);
        }

        @Override
        public Invitation[] newArray(int size) {
            return new Invitation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(invitationId);
        dest.writeString(groupId);
        dest.writeString(inviterId);
        dest.writeString(inviteeId);
        dest.writeString(date);
        dest.writeByte((byte) (accepted ? 1 : 0));
        dest.writeString(status);  // Parcelable로 상태 쓰기
    }

    // Getters and setters
    public String getInvitationId() { return invitationId; }
    public void setInvitationId(String invitationId) { this.invitationId = invitationId; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getInviterId() { return inviterId; }
    public void setInviterId(String inviterId) { this.inviterId = inviterId; }
    public String getInviteeId() { return inviteeId; }
    public void setInviteeId(String inviteeId) { this.inviteeId = inviteeId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
