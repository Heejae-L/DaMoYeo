package android.org.firebasetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import android.org.firebasetest.Diary; // Ensure this matches the package and class name of your Diary model


public class DiaryAdapter extends ArrayAdapter<Diary> {

    private Context mContext;
    private List<Diary> diaryList;

    public DiaryAdapter(@NonNull Context context, int resource, @NonNull List<Diary> objects) {
        super(context, resource, objects);
        mContext = context;
        diaryList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.diary_list_item, parent, false);
        }

        Diary currentDiary = diaryList.get(position);

        TextView date = listItem.findViewById(R.id.text_view_date);
        TextView content = listItem.findViewById(R.id.text_view_content);
        TextView weather = listItem.findViewById(R.id.text_view_weather);

        date.setText(currentDiary.getDate());
        content.setText(currentDiary.getContent());
        weather.setText(currentDiary.getWeather());

        return listItem;
    }
}
