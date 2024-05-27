package android.org.firebasetest;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Diary implements Parcelable {
    private String diaryId;
    private String authorId;
    private String date;
    private String title;
   // private String weather;
    //private String feeling;
   private int weatherImageId;
    private int moodImageId;
    private String location;
    private List<String> imageUrls;
    private String voice;
    private String content;
    private boolean isVoice;



    // 기본 생성자
    public Diary() {
    }

    // 모든 필드를 포함하는 생성자
    public Diary(String diaryId, String authorId, String date,String title, String location, List<String> imageUrls, String voice, String content,boolean isVoice,int weatherImageId, int moodImageId) {
        this.diaryId = diaryId;
        this.authorId = authorId;
        this.date = date;
        this.title = title;
        this.location = location;
        this.imageUrls = imageUrls;
        this.voice = voice;
        this.content = content;
        this.isVoice = isVoice;
        this.weatherImageId = weatherImageId;
        this.moodImageId = moodImageId;


    }

    // Parcelable 생성자
    protected Diary(Parcel in) {
        diaryId = in.readString();
        authorId = in.readString();
        date = in.readString();
        title = in.readString();
       // weather = in.readString();
        //feeling = in.readString();
        location = in.readString();
        imageUrls = in.createStringArrayList();
        voice = in.readString();
        content = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isVoice = in.readBoolean();
        }
        weatherImageId = in.readInt();
        moodImageId = in.readInt();

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
       // dest.writeString(weather);
        //dest.writeString(feeling);
        dest.writeString(location);
        dest.writeStringList(imageUrls);
        dest.writeString(voice);
        dest.writeString(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isVoice);
        }
        dest.writeInt(moodImageId);
        dest.writeInt(weatherImageId);
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

    /*
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


     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public Boolean getIsVoice() {
        return isVoice;
    }

    public void setIsVoice(Boolean isVoice) {
        this.isVoice = isVoice;
    }

    public int getWeatherImageId() {
        return weatherImageId;
    }

    public void setWeatherImageId(int weatherImageId) {
        this.weatherImageId = weatherImageId;
    }

    public int getMoodImageId() {
        return moodImageId;
    }

    public void setMoodImageId(int moodImageId) {
        this.moodImageId = moodImageId;
    }

}
