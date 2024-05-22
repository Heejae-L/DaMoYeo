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

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    private Context context;
    private List<Memo> memos;
    private MemoManager memoManager = new MemoManager();

    public MemoAdapter(Context context, List<Memo> memos) {
        this.context = context;
        this.memos = memos;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_list_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memos.get(position);
        holder.textViewDate.setText(memo.getDate());
        holder.textViewTitle.setText(memo.getTitle());
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void updateMemos(List<Memo> newMemos) {
        memos.clear();
        memos.addAll(newMemos);
        notifyDataSetChanged();
    }

    class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewTitle;

        public MemoViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewTitle = itemView.findViewById(R.id.text_view_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Memo memo = memos.get(position);
                        Intent intent = new Intent(context, MemoActivity.class); // Change this if MemoActivity exists
                        intent.putExtra("memo", memo);  // Passing the Memo object
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
                Memo memoToDelete = memos.get(position);
                showDeleteDialog(memoToDelete, position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showDeleteDialog(Memo memo, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this memo?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMemo(memo, position))
                .setNegativeButton("Cancel", (dialog, which) -> notifyItemChanged(position))
                .create().show();
    }

    private void deleteMemo(Memo memo, int position) {
        memos.remove(position);
        notifyItemRemoved(position);
        memoManager.deleteMemo(memo.getMemoId()); // Make sure this method exists in your MemoManager
    }
}
