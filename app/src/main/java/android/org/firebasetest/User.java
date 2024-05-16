package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    private String userId;
    private String name;
    private String email;
    private int age;
    private String bio;
    private int isMale;
    private String profileImageUrl; // 프로필 이미지 URL 필드 추가

    public User() {
        // Firebase에서 사용을 위한 기본 생성자
    }

    public User(String userId, String name, String email, int age, String bio, int isMale ,String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.bio = bio;
        this.isMale = isMale;
        this.profileImageUrl = profileImageUrl;
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        email = in.readString();
        age = in.readInt();
        bio = in.readString();
        isMale = in.readInt();
        profileImageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeInt(age);
        dest.writeString(bio);
        dest.writeInt(isMale);
        dest.writeString(profileImageUrl);
    }



    // getter and setter methods
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public int getSex() { return isMale; }
    public void setSex(int isMale) { this.isMale = isMale; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
