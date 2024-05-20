package android.org.firebasetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileImageManager {

    private final StorageReference storageReference;

    public ProfileImageManager() {
        // Firebase Storage 참조 초기화
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    // 사용자 프로필 이미지를 로드하는 메서드
    public void loadProfileImage(Context context, ImageView imageView, String userId) {
        if (userId != null && !userId.isEmpty()) {
            String imagePath = "users/" + userId + "/profile.jpeg";
            StorageReference imageRef = storageReference.child(imagePath);

            final long ONE_MEGABYTE = 1024 * 1024;

            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
                Log.d("ProfileImageManager", "Image loaded successfully.");
            }).addOnFailureListener(exception -> {
                // 로드 실패시 기본 이미지 설정
                imageView.setImageResource(R.drawable.default_profile_image);
                Toast.makeText(context, "Error loading image: " + exception.getMessage() + ". Default image set.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(context, "User ID is null or empty", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
        }
    }


    // 새 이미지를 업로드하고 기존 이미지를 교체하는 메서드
    public void saveProfileImage(Context context, Uri selectedImageUri, String userId) {
        Log.d("saveProfileImage:","userId:"+userId);
        if (selectedImageUri != null && userId != null) {
            StorageReference fileRef = storageReference.child("users/" + userId + "/profile.jpeg");

            // Attempt to delete the old image, but proceed anyway if the deletion fails.
            fileRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("ProfileImageManager", "Old image deleted.");
                uploadNewImage(context, fileRef, selectedImageUri, userId);
            }).addOnFailureListener(e -> {
                Log.e("ProfileImageManager", "Failed to delete old image, uploading new image anyway.");
                uploadNewImage(context, fileRef, selectedImageUri, userId);  // Proceed to upload new image even if deletion fails
            });
        } else {
            Toast.makeText(context, "Invalid user ID or image Uri.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNewImage(Context context, StorageReference fileRef, Uri imageUri, String userId) {
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveData(context, userId, uri.toString());
                Toast.makeText(context, "Profile image updated successfully.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to upload new image.", Toast.LENGTH_SHORT).show();
        });
    }


    // 업로드된 이미지 URL을 SharedPreferences에 저장하는 메서드
    private void saveData(Context context, String userId, String imageUrl) {
        SharedPreferences prefs = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(userId + "_imageUrl", imageUrl);
        editor.apply();
    }
}
