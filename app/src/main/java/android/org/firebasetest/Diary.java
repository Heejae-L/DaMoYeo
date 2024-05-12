package android.org.firebasetest;
public class Diary {
    private String authorId;
    private String date;
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
    public Diary(String authorId, String date, String weather, String feeling, String location, String image, String voice, String content) {
        this.authorId = authorId;
        this.date = date;
        this.weather = weather;
        this.feeling = feeling;
        this.location = location;
        this.image = image;
        this.voice = voice;
        this.content = content;
    }

    // 게터와 세터 메소드
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
