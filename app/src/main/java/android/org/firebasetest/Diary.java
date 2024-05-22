package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

public class Diary implements Parcelable {
    private String diaryId;
    private String authorId;
    private String date;
    private String title;
    private String weather;
    private String feeling;
    private String location;
    private String image;
    private String voice;
    private String content;

    // 기본 생성자
    public Diary() {
    }

    // 모든 필드를 포함하는 생성자
    public Diary(String diaryId, String authorId, String date,String title, String weather, String feeling, String location, String image, String voice, String content) {
        this.diaryId = diaryId;
        this.authorId = authorId;
        this.date = date;
        this.title = title;
        this.weather = weather;
        this.feeling = feeling;
        this.location = location;
        this.image = image;
        this.voice = voice;
        this.content = content;
    }

    // Parcelable 생성자
    protected Diary(Parcel in) {
        diaryId = in.readString();
        authorId = in.readString();
        date = in.readString();
        title = in.readString();
        weather = in.readString();
        feeling = in.readString();
        location = in.readString();
        image = in.readString();
        voice = in.readString();
        content = in.readString();
    }

    public static final Parcelable.Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(diaryId);
        dest.writeString(authorId);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(weather);
        dest.writeString(feeling);
        dest.writeString(location);
        dest.writeString(image);
        dest.writeString(voice);
        dest.writeString(content);
    }

    // 게터와 세터 메소드
    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
