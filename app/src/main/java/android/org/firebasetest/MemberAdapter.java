package android.org.firebasetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<User> members;
    private Context context;

    public MemberAdapter(Context context, List<User> members) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = members.get(position);
        holder.textViewMemberName.setText(member.getName());
        new ProfileImageManager().loadProfileImage(context, holder.imageViewProfile, member.getUserId());
        holder.itemView.setOnClickListener(v -> Toast.makeText(context, member.getName(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView textViewMemberName;

        MemberViewHolder(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewMemberName = itemView.findViewById(R.id.textViewMemberName);
        }
    }
}
