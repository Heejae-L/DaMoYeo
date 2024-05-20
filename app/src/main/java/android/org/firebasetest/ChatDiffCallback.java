package android.org.firebasetest;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

class ChatDiffCallback extends DiffUtil.Callback {
    private final List<ChatMessage> oldList;
    private final List<ChatMessage> newList;

    public ChatDiffCallback(List<ChatMessage> oldList, List<ChatMessage> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // 메시지 ID 또는 고유 식별자를 비교

        return Objects.equals(oldList.get(oldItemPosition).getGroupId(), newList.get(newItemPosition).getGroupId());    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // 실제 내용 비교
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }


}
