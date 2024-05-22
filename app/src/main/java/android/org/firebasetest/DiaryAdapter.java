package android.org.firebasetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private Context context;
    private List<Diary> diaries;
    private DiaryManager diaryManager = new DiaryManager();

    public DiaryAdapter(Context context, List<Diary> diaries) {
        this.context = context;
        this.diaries = diaries;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diary_list_item, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaries.get(position);
        holder.textViewDate.setText(diary.getDate());
        holder.textViewContent.setText(diary.getContent());
        holder.textViewWeather.setText(diary.getWeather());
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    public void updateDiaries(List<Diary> newDiaries) {
        diaries.clear();
        diaries.addAll(newDiaries);
        notifyDataSetChanged();
    }

    class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewContent, textViewWeather;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewContent = itemView.findViewById(R.id.text_view_content);
            textViewWeather = itemView.findViewById(R.id.text_view_weather);

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Diary diary = diaries.get(position);
                        Intent intent = new Intent(context, DiaryActivity.class);
                        intent.putExtra("diary", diary);  // 다이어리 객체 전달
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Diary diaryToDelete = diaries.get(position);

                // Show confirmation dialog
                showDeleteDialog(diaryToDelete, position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showDeleteDialog(Diary diary, int position) {
        // AlertDialog for confirmation
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this diary?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDiary(diary, position))
                .setNegativeButton("Cancel", (dialog, which) -> notifyItemChanged(position))
                .create().show();
    }

    private void deleteDiary(Diary diary, int position) {
        diaries.remove(position);
        notifyItemRemoved(position);
        diaryManager.deleteDiary(diary.getAuthorId(),diary.getDiaryId()); // Assuming DiaryManager has a method to delete diary from DB
    }

}
