package com.example.gitcommitchecker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text1);
        button = findViewById(R.id.button);

        Observable<Void> observable = Observable.create(subscriber -> {
            subscriber.onComplete();
        });

        button.setOnClickListener(view -> {
            observable.subscribe(gitHubAPIObserver);
        });
    }

    Observer<Void> gitHubAPIObserver = new Observer<Void>() {
        String lastPushDate = "This User Nothing Pushed";

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

            retrofitAPI.listRepos("corn1200", "pushed", 1)
                    .enqueue(new Callback<List<Repo>>() {
                        @Override
                        public void onResponse(Call<List<Repo>> call,
                                               Response<List<Repo>> response) {
                            if (response.isSuccessful()) {
                                List<Repo> data = response.body();
                                lastPushDate = data.get(0).getPushedAt();

                                SimpleDateFormat dateFormat =
                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = null;
                                try {
                                    date = dateFormat.parse(lastPushDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Log.i("response", date.toString());

                                textView.setText(date.toString());
                                Log.i("this date", new Date().toString());

                                SimpleDateFormat compDayFormat = new SimpleDateFormat("yyyyMMdd");
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