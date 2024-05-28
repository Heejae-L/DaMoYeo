package android.org.firebasetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    private List<Group> groups;
    private GroupManager groupManager = new GroupManager();
    public String userId;

    public GroupAdapter(Context context, List<Group> groups, String uId) {
        this.context = context;
        this.groups = groups;
        this.userId = uId;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.textViewTitle.setText(group.getTitle());
        holder.textViewDescription.setText(group.getDescription());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> newGroups) {
        this.groups.clear();
        this.groups.addAll(newGroups);
        notifyDataSetChanged(); // This is crucial
    }


    class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription;

        public GroupViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Group group = groups.get(position);
                        Intent intent = new Intent(context, GroupActivity.class); // Change this if MemoActivity exists
                        intent.putExtra("userId",userId);
                        intent.putExtra("group", group);  // Passing the Memo object
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
                Group groupToDelete = groups.get(position);
                showDeleteDialog(groupToDelete, position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showDeleteDialog(Group group, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this group?")
                .setPositiveButton("Delete", (dialog, which) -> deleteGroup(group, position))
                .setNegativeButton("Cancel", (dialog, which) -> notifyItemChanged(position))
                .create().show();
    }

    private void deleteGroup(Group group, int position) {
        groups.remove(position);
        notifyItemRemoved(position);
        groupManager.deleteGroup(group.getGroupId()); // Make sure this method exists in your MemoManager
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}

