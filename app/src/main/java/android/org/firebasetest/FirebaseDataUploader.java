package android.org.firebasetest;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDataUploader {

    private DatabaseReference database;

    public FirebaseDataUploader() {
        // Firebase 데이터베이스 인스턴스 초기화
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void uploadData() {
        // 사용자 데이터 저장
        DatabaseReference usersRef = database.child("users");
        usersRef.child("user1").setValue(new User("홍길동", "010-1234-5678", "base64EncodedImageString", "1990-01-01", 30, "male", "hashedPassword"));

        // 메모 데이터 저장
        DatabaseReference memosRef = database.child("memos");
        memosRef.child("memo1").setValue(new Memo("happy", "2024-05-10", "오늘은 매우 행복한 날!", "base64EncodedImageString", "user1"));

        // 그룹 데이터 저장
        DatabaseReference groupsRef = database.child("groups");
        groupsRef.child("group1").setValue(new Group("코딩 스터디 그룹", "코딩 파이터", "2024-05-10"));

        // 캘린더 데이터 저장
        DatabaseReference calendarsRef = database.child("calendars");
        calendarsRef.child("calendar1").setValue(new Calendar(1, "memo1", "2024-05-10", "2024-05-11", "휴가", "user1"));

        // 다이어리 엔트리 데이터 저장
        DatabaseReference diaryEntriesRef = database.child("diary_entries");
        diaryEntriesRef.child("user1_20240510").setValue(new DiaryEntry("sunny", "happy", "서울", "base64EncodedImageString", "audioFileLink", "일기 내용"));

        // 행복함 상자 데이터 저장
        DatabaseReference happinessBoxesRef = database.child("happiness_boxes");
        happinessBoxesRef.child("user1_20240510").setValue(new HappinessBox("user1_20240510"));

        // 알림 데이터 저장
        DatabaseReference notificationsRef = database.child("notifications");
        notificationsRef.child("notification1").setValue(new Notification("user1", "새 알림이 도착했습니다.", 1, "2024-05-10T12:00:00Z"));
    }

    // 각 데이터 클래스 정의
    class User {
        public String name, number, image, birth;
        public int age;
        public String sex, password;

        public User(String name, String number, String image, String birth, int age, String sex, String password) {
            this.name = name;
            this.number = number;
            this.image = image;
            this.birth = birth;
            this.age = age;
            this.sex = sex;
            this.password = password;
        }
    }

    class Memo {
        public String feeling, date, body_text, image, authorID;

        public Memo(String feeling, String date, String body_text, String image, String authorID) {
            this.feeling = feeling;
            this.date = date;
            this.body_text = body_text;
            this.image = image;
            this.authorID = authorID;
        }
    }

    class Group {
        public String description, title, date;

        public Group(String description, String title, String date) {
            this.description = description;
            this.title = title;
            this.date = date;
        }
    }

    class Calendar {
        public int privateFlag;
        public String memo, start_date, end_date, title, userID;

        public Calendar(int privateFlag, String memo, String start_date, String end_date, String title, String userID) {
            this.privateFlag = privateFlag;
            this.memo = memo;
            this.start_date = start_date;
            this.end_date = end_date;
            this.title = title;
            this.userID = userID;
        }
    }

    class DiaryEntry {
        public String entry_weather, entry_mood, entry_location, entry_image, entry_voice, entry_content;

        public DiaryEntry(String entry_weather, String entry_mood, String entry_location, String entry_image, String entry_voice, String entry_content) {
            this.entry_weather = entry_weather;
            this.entry_mood = entry_mood;
            this.entry_location = entry_location;
            this.entry_image = entry_image;
            this.entry_voice = entry_voice;
            this.entry_content = entry_content;
        }
    }

    class HappinessBox {
        public String entry_reference;

        public HappinessBox(String entry_reference) {
            this.entry_reference = entry_reference;
        }
    }

    class Notification {
        public String authorID, content;
        public int vibrate;
        public String timestamp;

        public Notification(String authorID, String content, int vibrate, String timestamp) {
            this.authorID = authorID;
            this.content = content;
            this.vibrate = vibrate;
            this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        FirebaseDataUploader uploader = new FirebaseDataUploader();
        uploader.uploadData();
    }
}
