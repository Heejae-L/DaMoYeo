package android.org.firebasetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private int[] imageIds;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    public ImageAdapter(Context context, int[] imageIds) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.imageIds = imageIds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(imageIds[position % imageIds.length]);
    }

    @Override
    public int getItemCount() {
        return imageIds.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // 이미지를 클릭한 위치(position)를 기반으로 선택된 이미지의 리소스 ID를 가져옵니다.
            int selectedImageId = imageIds[position % imageIds.length];
            // MainActivity로 선택된 이미지의 리소스 ID를 전달합니다.
            if (mClickListener != null) {
                mClickListener.onItemClick(selectedImageId);
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int selectedImageId);
    }
}