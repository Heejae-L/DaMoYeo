package android.org.firebasetest;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<Date> dayList;
    private DateSelectedListener  listener;
    private Context context;

    private int selectedItem = -1;  // 선택된 아이템의 위치 초기화. 처음에는 어떤 것도 선택되지 않았다고 가정.
    // 날짜 선택 리스너 인터페이스 정의
    public interface DateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }

    // 리스너 설정 메소드
    public void setDateSelectedListener(DateSelectedListener listener) {
        this.listener = listener;
    }

    public CalendarAdapter(Context context, ArrayList<Date> dayList){
        this.dayList = dayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position){
        //날짜 변수에 담기
        Date monthDate = dayList.get(position);

        //달력 초기화
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        //현재 년 월
        int currentDay = CalendarUtil.selectedDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH)+1;
        int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);
        //넘어온 데이터
        int displayDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        //비교해서 년, 월 다르면 연한색
        if(displayMonth == currentMonth && displayYear==currentYear){
            holder.parentView.setBackgroundColor(Color.WHITE);
            //날짜까지 맞으면 색상 표시
            if(displayDay == currentDay) {
                holder.itemView.setBackgroundColor(Color.parseColor("#C0C0C0"));
            }
            // 선택한 날짜의 색상 변경
            if (selectedItem == position) {
                // 선택된 아이템의 배경색 설정
                holder.itemView.setBackgroundColor(Color.parseColor("#C0C0C0"));
            } else {
                // 다른 아이템은 배경색을 하얀색으로 설정
                holder.itemView.setBackgroundColor(Color.WHITE);
            }
        }else{
            holder.parentView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }

        //날짜 변수에 담기
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        holder.dayText.setText(String.valueOf(dayNo));

        //텍스트 색상 지정
        if((position+1)%7==0){  //토요일
            holder.dayText.setTextColor(Color.BLUE);
        }else if(position==0||(position+1)%7==1){   //일요일
            holder.dayText.setTextColor(Color.RED);
        }

        //클릭시 이날짜 불러오기?
        int iYear = dateCalendar.get(Calendar.YEAR);
        int iMonth = dateCalendar.get(Calendar.MONTH) + 1;  // Calendar.MONTH는 0부터 시작하므로 +1
        int iDay = dateCalendar.get(Calendar.DAY_OF_MONTH);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전에 선택된 아이템 위치 업데이트
                int previousItem = selectedItem;
                selectedItem = position;

                // 이전 선택된 아이템과 현재 선택된 아이템을 갱신
                notifyItemChanged(previousItem);
                notifyItemChanged(selectedItem);

                // 날짜 선택 리스너 이벤트 호출
                if (listener != null) {
                    listener.onDateSelected(iYear, iMonth, iDay);
                }
                //String yearMonDay = iYear + "년" + iMonth + "월" + iDay + "일";
                //Toast.makeText(holder.itemView.getContext(), yearMonDay, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateDayList(ArrayList<Date> newDayList) {
        dayList.clear();
        dayList.addAll(newDayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        //초기화
        TextView dayText;
        View parentView;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);
            parentView = itemView.findViewById(R.id.parentView);
        }
    }
}
