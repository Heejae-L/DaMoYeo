package android.org.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        TextView tvEventDate = findViewById(R.id.tvEventDate);
        TextView tvEventDetails = findViewById(R.id.textview_main_result);

        // 버튼 찾기
        Button backToMainButton = findViewById(R.id.backToMainButton);


        // 인텐트에서 이벤트 정보를 가져옵니다.
        String eventDetails = getIntent().getStringExtra("eventDetails");
        int year = getIntent().getIntExtra("YEAR", 0);
        int month = getIntent().getIntExtra("MONTH", 0) + 1; // month 값 조정 (0-11 to 1-12)
        int day = getIntent().getIntExtra("DAY", 0);

// 날짜 정보가 있을 경우 TextView에 표시
        if (year != 0 && month != 0 && day != 0) {
            String date = year + "년 " + month + "월 " + day + "일";
            tvEventDate.setText(date);
        } else {
            tvEventDate.setText("날짜 정보 없음");
        }

        // 이벤트 상세 정보 설정
        if (eventDetails != null) {
            tvEventDetails.setText(eventDetails);
        } else {
            tvEventDetails.setText("이벤트 정보가 없습니다.");
        }

        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 메인 액티비티로 돌아가기
                Intent intent = new Intent(EventDetailsActivity.this, MainActivity.class);
                // FLAG_ACTIVITY_CLEAR_TOP 플래그는 액티비티 스택에 이미 MainActivity가 있으면 그 위의 모든 액티비티를 종료시키고 해당 액티비티로 이동
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}
