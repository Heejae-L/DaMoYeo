package android.org.firebasetest;

public class User {
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
