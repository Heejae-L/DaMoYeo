package android.org.firebasetest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.ViewHolder> {
    private ArrayList<EventDetail> details;
    private int selectedItem = -1; // 선택되지 않은 초기 상태
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EventDetail eventDetail);
    }


    public EventDetailsAdapter(ArrayList<EventDetail> details, OnItemClickListener listener) {
        this.details = details;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        EventDetail detail = details.get(position);

        // 날짜와 시간을 한국 시간대에 맞게 포맷팅
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String formattedStartTime = dateTimeFormat.format(new Date(detail.getStartTime().getValue()));
        String formattedEndTime = detail.getEndTime() != null ? dateTimeFormat.format(new Date(detail.getEndTime().getValue())) : "";

        holder.detailText.setText(String.format("%s\n(%s - %s)", detail.getSummary(), formattedStartTime, formattedEndTime));

        // 배경색 설정
        if (selectedItem == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFD54F"));  // 선택된 아이템에 대한 색상
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);  // 다른 아이템은 투명색
        }

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousItem = selectedItem;
                selectedItem = position;

                // 이전 선택된 아이템과 현재 선택된 아이템을 갱신
                notifyItemChanged(previousItem);
                notifyItemChanged(selectedItem);

                if (listener != null) {
                    listener.onItemClick(detail);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return details.size(); // ArrayList의 크기를 반환
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView detailText;

        ViewHolder(View itemView) {
            super(itemView);
            detailText = itemView.findViewById(R.id.detailText); // 뷰 바인딩
        }
    }
}
