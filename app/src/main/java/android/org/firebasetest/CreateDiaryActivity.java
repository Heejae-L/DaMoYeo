package android.org.firebasetest;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateDiaryActivity extends AppCompatActivity {

    private static final int PICK_IMAGES_REQUEST = 1;


    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private DiaryManager diaryManager;
    private FirebaseAuth mAuth;
    private Diary diary;
    private String diaryId;
    private boolean isEditMode = false;


    public EditText editTextTitle, editTextContent,editTextLocation;
    public DatePicker diaryDatePicker;
    private LinearLayout imagesLayout;

    public Button buttonSaveDiary;


    //Floating menu
    public FloatingActionButton fab_main;
    public FloatingActionButton fab_weather;
    public FloatingActionButton fab_mood;
    public FloatingActionButton fab_location;
    public FloatingActionButton fab_image;
    public FloatingActionButton fab_voice;
    public Animation fab_open, fab_close;
    public boolean fabMain_status = false;
    public String selectedDate;

    private List<Uri> imageUris;
    private List<String> imageUrls;

    private int selectedWeatherImageId = 0; // 기본값으로 0 설정
    private int selectedMoodImageId = 0; // 기본값으로 0 설정
    public ImageView selectedWeatherImageView;
    public ImageView selectedMoodImageView;
    public int[] weatherImageIds = {R.drawable.weather1, R.drawable.weather2, R.drawable.weather3, R.drawable.weather4};
    public int[] moodImageIds = {R.drawable.icon_draw, R.drawable.mood2, R.drawable.mood3, R.drawable.mood4};

    //voice 관련
    public VoiceHandler voiceHandler;
    public ImageView recorded_voice;
    public boolean isRecording = false;
    public String audioFilePath; // 녹음된 파일의 경로




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d("CreateDiary","start" );

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationHelper.setupToolbar(toolbar, this);

        editTextTitle = findViewById(R.id.editTextTitle);
        diaryDatePicker = findViewById(R.id.pickerDate);
        editTextContent = findViewById(R.id.editTextContent);
        editTextLocation = findViewById(R.id.editTextLocation);
        imagesLayout = findViewById(R.id.imagesLayout);
        buttonSaveDiary = findViewById(R.id.buttonSaveDiary);

        selectedWeatherImageView = findViewById(R.id.selected_weather);
        selectedMoodImageView = findViewById(R.id.selected_mood);

        fab_main = findViewById(R.id.fab_main);
        fab_weather = findViewById(R.id.fab_weather);
        fab_mood = findViewById(R.id.fab_mood);
        fab_location = findViewById(R.id.fab_location);
        fab_image = findViewById(R.id.fab_image);
        fab_voice = findViewById(R.id.fab_voice);

        imageUris = new ArrayList<>();
        imageUrls = new ArrayList<>();
        diary = getIntent().getParcelableExtra("diary");
        diaryId  =getIntent().getStringExtra("diaryId");

        //Voice 초기화
        recorded_voice = findViewById(R.id.recorded_voice);
        voiceHandler = new VoiceHandler(this);

        Log.e("CreateDiary"," "+diaryId );

        if (diaryId != null) {
            isEditMode = true;
            loadDiaryEntry();
            buttonSaveDiary.setText("Update");
        } else {
            isEditMode = false;
            buttonSaveDiary.setText("Save");
        }


        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editTextContent.getText().toString().trim().length() > 3) {
                    buttonSaveDiary.setEnabled(true);
                } else {
                    buttonSaveDiary.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //floating menu option
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
            }
        });

        fab_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageBottomSheet(weatherImageIds, selectedWeatherImageView,true);
                Toast.makeText(CreateDiaryActivity.this, "weather 버튼 클릭", Toast.LENGTH_SHORT).show();

            }
        });

        fab_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageBottomSheet(moodImageIds, selectedMoodImageView,false);
                Toast.makeText(CreateDiaryActivity.this, "mood 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        // locationHandler = new LocationHandler(this);

        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateDiaryActivity.this, "location 버튼 클릭", Toast.LENGTH_SHORT).show();
                //locationHandler.searchLocation();
                //구글 지도를 사용해서
            }
        });

        fab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateDiaryActivity.this, "image 버튼 클릭", Toast.LENGTH_SHORT).show();
                selectImages();
            }
        });

        //녹음된 이미지 버튼 누르면 재생
        recorded_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceHandler.playAudio();
            }
        });


        //녹음 하려고 누르는 버튼
        // 녹음 버튼 클릭 시 녹음 시작 또는 중지
        fab_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    // 녹음 시작
                    voiceHandler.startRecording();
                    isRecording = true;
                    // 녹음 버튼 이미지 변경 등의 UI 업데이트 가능

                    // 녹음된 파일이 존재한다면 recorded_voice 이미지 뷰를 활성화합니다.
                    if (voiceHandler.getAudioFilePath() != null) {
                        recorded_voice.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 녹음 중지
                    voiceHandler.stopRecording();
                    isRecording = false;
                    // 녹음 버튼 이미지 변경 등의 UI 업데이트 가능

                    // 녹음이 중지되면 recorded_voice 이미지 뷰를 비활성화합니다.
                    recorded_voice.setVisibility(View.GONE);
                }
            }
        });



        buttonSaveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateDiaryEntry();

            }
        });


    }

    private void loadDiaryEntry() {

        editTextTitle.setText(diary.getTitle());
        editTextLocation.setText(diary.getLocation());
        editTextContent.setText(diary.getContent());

        // Set DatePicker to diary date
        String[] dateParts = diary.getDate().split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based in DatePicker
        int day = Integer.parseInt(dateParts[2]);

        diaryDatePicker.init(year, month, day, null);

        if (diary.getImageUrls() != null) {
            imageUrls.addAll(diary.getImageUrls());
            for (String url : diary.getImageUrls()) {
                addImageView(Uri.parse(url));
            }
        }

        // 이미지 뷰에 리소스 ID로 이미지 설정
        if (diary.getWeatherImageId() != 0) {
            selectedWeatherImageView.setImageResource(diary.getWeatherImageId());
        }
        if (diary.getMoodImageId() != 0) {
            selectedMoodImageView.setImageResource(diary.getMoodImageId());
        }

    }

    private void saveOrUpdateDiaryEntry() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = user.getUid();
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String date = formatDate(diaryDatePicker.getYear(), diaryDatePicker.getMonth(), diaryDatePicker.getDayOfMonth());


        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Diary diary = new Diary(diaryId, userId, date, title, location, imageUrls, null, content, false, selectedWeatherImageId, selectedMoodImageId);

        DiaryManager diaryManager = new DiaryManager();
        if (isEditMode) {
            // 업데이트 모드
            diaryManager.saveDiary(userId, diary);

        } else {
            // 새 다이어리 저장
            diaryId = diaryManager.getDatabase().push().getKey();  // 새 ID 생성
            diary.setDiaryId(diaryId);  // 새 ID 설정
            diaryManager.saveDiary(userId, diary);

        }

        finish();

    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    public void toggleFab() {
        if (fabMain_status) {
            // 플로팅 액션 버튼 닫기
            ObjectAnimator[] animations = {
                    ObjectAnimator.ofFloat(fab_weather, "translationY", 0f),
                    ObjectAnimator.ofFloat(fab_mood, "translationY", 0f),
                    ObjectAnimator.ofFloat(fab_location, "translationY", 0f),
                    ObjectAnimator.ofFloat(fab_image, "translationY", 0f),
                    ObjectAnimator.ofFloat(fab_voice, "translationY", 0f)
            };
            for (ObjectAnimator animation : animations) {
                animation.start();
            }
            fab_main.setImageResource(R.drawable.icon_menu);
        } else {
            // 플로팅 액션 버튼 열기
            ObjectAnimator[] animations = {
                    ObjectAnimator.ofFloat(fab_weather, "translationY", -200f),
                    ObjectAnimator.ofFloat(fab_mood, "translationY", -400f),
                    ObjectAnimator.ofFloat(fab_location, "translationY", -600f),
                    ObjectAnimator.ofFloat(fab_image, "translationY", -800f),
                    ObjectAnimator.ofFloat(fab_voice, "translationY", -1000f)
            };
            for (ObjectAnimator animation : animations) {
                animation.start();
            }
            fab_main.setImageResource(R.drawable.icon_add_menu);
        }
        fabMain_status = !fabMain_status;
    }

    public void showImageBottomSheet(final int[] imageIds, final ImageView selectedImageView, final boolean isWeather) {
        ImageBottomSheetFragment bottomSheetFragment = new ImageBottomSheetFragment();
        bottomSheetFragment.setImageIds(imageIds); // 이미지 정보 전달
        bottomSheetFragment.setOnImageSelectedListener(new ImageBottomSheetFragment.OnImageSelectedListener() {
            @Override
            public void onImageSelected(int selectedImageId) {
                selectedImageView.setImageResource(selectedImageId);
                if (isWeather) {
                    selectedWeatherImageId = selectedImageId; // 날씨 이미지 ID 저장
                } else {
                    selectedMoodImageId = selectedImageId; // 기분 이미지 ID 저장
                }
            }
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }




    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && i < 3; i++) {  // 최대 3개 이미지 선택
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    addImageView(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                addImageView(imageUri);
            }
        }
    }

    private void addImageView(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));  // 크기 조절
        imageView.setImageURI(imageUri);
        imagesLayout.addView(imageView);
    }
/*
    private void saveDiaryToDatabase(String title, String content, String date, String userId, List<String> imageUrls) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("diaryEntries");
        String entryId = isEditMode ? diaryId : databaseRef.push().getKey();

        Diary diary = new Diary(diaryId, userId, date, title, location, imageUrls, null, content, false, weather,mood);

        databaseRef.child(entryId).setValue(diary)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateDiaryActivity.this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CreateDiaryActivity.this, "Failed to save diary", Toast.LENGTH_SHORT).show());
    }

    private void uploadImages(String title, String content, String date, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("diaryImages");
        List<String> uploadedImageUrls = new ArrayList<>();

        for (Uri imageUri : imageUris) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    uploadedImageUrls.add(uri.toString());
                    if (uploadedImageUrls.size() == imageUris.size()) {
                        saveDiaryToDatabase(title, content, date, userId, uploadedImageUrls);
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(CreateDiaryActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

 */

    private String getFileExtension(Uri uri) {
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
        return extension != null ? extension : "jpg";  // 기본값을 jpg로 설정
    }






}