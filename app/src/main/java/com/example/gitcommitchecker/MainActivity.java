package com.example.gitcommitchecker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    int alarmRequestCode = 1;
    int hourOfDay;
    int minute;

    TextView textView;
    Button button;
    TimePicker timePicker;
    Button setAlarmButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        hourOfDay = sharedPreferences.getInt(getString(R.string.alarm_hour), 0);
        minute = sharedPreferences.getInt(getString(R.string.alarm_minute), 0);

//        상호작용할 뷰를 바인딩합니다
        textView = findViewById(R.id.lastCommitDate);
        button = findViewById(R.id.requestButton);
        timePicker = findViewById(R.id.timePicker);
        setAlarmButton = findViewById(R.id.setAlarmButton);

//        객체를 구독하는 옵저버에 동작을 전달합니다
        Observable<Void> observable = Observable.create(subscriber -> {
            subscriber.onComplete();
        });

//        리퀘스트 버튼 클릭 시 옵저버가 옵저베이블 객체를 구독하도록 합니다
        button.setOnClickListener(view -> {
            observable.subscribe(gitHubAPIObserver);
        });

        timePicker.setHour(hourOfDay);
        timePicker.setMinute(minute);

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            this.hourOfDay = hourOfDay;
            this.minute = minute;
            setAlarmButton.setEnabled(true);
        });

        setAlarmButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.alarm_hour), hourOfDay);
            editor.putInt(getString(R.string.alarm_minute), minute);
            editor.apply();

            String commitAlarmMsg = hourOfDay + ":" + minute + " to commit alarm";
            Toast.makeText(this, commitAlarmMsg, Toast.LENGTH_SHORT).show();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast
                    (this, alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), alarmIntent);
        });
    }

    //    옵저버가 실행할 동작을 정의합니다
    Observer<Void> gitHubAPIObserver = new Observer<Void>() {
        String lastPushDate = "This User Nothing Pushed";

        @Override
        public void onSubscribe(@NonNull Disposable d) {
//            리퀘스트를 보낼 주소와 retrofit 객체를 설정합니다
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

//            API 인터페이스 객체를 생성합니다
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

//            API 의 URI 값과 get 쿼리 값을 전달하고 리퀘스트합니다
            retrofitAPI.listRepos("corn1200", "pushed", 1)
                    .enqueue(new Callback<List<Repo>>() {
                        @Override
                        public void onResponse(Call<List<Repo>> call,
                                               Response<List<Repo>> response) {
//                            리퀘스트가 정상적으로 동작하여 결과값이 있으면 아래 작업을 실행합니다
                            if (response.isSuccessful()) {
//                                Json 데이터를 리스트로 만들고 유저의 마지막 커밋의 날짜 정보를 저장합니다
                                List<Repo> data = response.body();
                                lastPushDate = data.get(0).getPushedAt();

//                                날짜 정보의 포맷을 설정하고 해당 날짜 정보의 시간대를 입력합니다
                                SimpleDateFormat dateFormat =
                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

//                                String 날짜 정보를 Date 객체로 파싱합니다
                                Date date = null;
                                try {
                                    date = dateFormat.parse(lastPushDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Log.i("response", date.toString());

//                                마지막 커밋 날짜를 뷰에 출력합니다
                                textView.setText(date.toString());
                                Log.i("this date", new Date().toString());

//                                마지막 커밋 날짜와 현재 날짜를 같은 날짜인지 비교합니다
                                SimpleDateFormat compDayFormat =
                                        new SimpleDateFormat("yyyyMMdd");
                                Log.i("date compare", String.valueOf(compDayFormat.format(date)
                                        .compareTo(compDayFormat.format(new Date()))));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Repo>> call, Throwable t) {
                            Log.d("Request", "request 실패");

                            t.printStackTrace();
                        }
                    });
        }

        @Override
        public void onNext(@NonNull Void unused) {
            Log.d(Thread.currentThread().getName(), "onNext()를 호출할 필요가 없습니다");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(Thread.currentThread().getName(), "푸쉬 기록 호출 Observer Error");
        }

        @Override
        public void onComplete() {
            Log.d("Request", "request 성공");
        }
    };
}