package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ViewMemosFragment extends Fragment {
    private ListView listViewMemos;
    private ArrayAdapter<String> memoAdapter;
    private List<Memo> memos;
    private MemoManager memoManager;
    private String groupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_memos, container, false);

        listViewMemos = view.findViewById(R.id.listViewMemos);
        memos = new ArrayList<>();
        memoAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewMemos.setAdapter(memoAdapter);

        memoManager = new MemoManager();

        // Group ID should be set before adding the fragment or passed via arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("groupId")) {
            groupId = args.getString("groupId");
            loadMemosForGroup(groupId);
            Log.d("ViewMemoFragment","groupID:"+groupId);
        } else {
            Toast.makeText(getContext(), "Group ID is not specified", Toast.LENGTH_SHORT).show();
        }

        listViewMemos.setOnItemClickListener((parent, view1, position, id) -> {
            Memo selectedMemo = memos.get(position);
            Intent intent = new Intent(getActivity(), MemoActivity.class);
            intent.putExtra("memo", selectedMemo); // Passing the selected Memo object to MemoActivity
            startActivity(intent);
        });

        return view;
    }

    private void loadMemosForGroup(String groupId) {
        memoManager.fetchMemosByGroupId(groupId, new MemoManager.MemosCallback() {
            @Override
            public void onMemosRetrieved(List<Memo> retrievedMemos) {
                memos.clear();
                memoAdapter.clear();
                for (Memo memo : retrievedMemos) {
                    memos.add(memo);
                    memoAdapter.add(memo.getDate() + " - " + memo.getTitle());
                }
                memoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load memos: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
