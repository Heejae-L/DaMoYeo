package android.org.firebasetest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageBottomSheetFragment extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private OnImageSelectedListener imageSelectedListener;
    private int[] imageIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        // RecyclerView에 GridLayoutManager를 설정하여 이미지를 3개씩 2줄로 배열합니다.
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ImageAdapter(getContext(), imageIds);
        adapter.setClickListener(selectedImageId -> {
            // RecyclerView에서 이미지를 선택하면 MainActivity로 선택된 이미지의 리소스 ID를 전달합니다.
            if (imageSelectedListener != null) {
                imageSelectedListener.onImageSelected(selectedImageId);
            }
            // BottomSheet를 닫습니다.
            dismiss();
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            // 높이 설정
            bottomSheet.getLayoutParams().height = 600; // 원하는 높이로 설정 (600 픽셀로 설정 예시)
            behavior.setPeekHeight(600); // BottomSheet의 초기 높이 설정

        }
    }


    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.imageSelectedListener = listener;
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int selectedImageId);
    }

    public void setImageIds(int[] imageIds){
        this.imageIds = imageIds;
    }

}
