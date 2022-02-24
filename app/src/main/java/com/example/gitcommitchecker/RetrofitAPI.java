package com.example.gitcommitchecker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//    호출할 API 들에 대해 정의해놓은 인터페이스입니다
public interface RetrofitAPI {
//    user 의 repository 정보를 불러 오는 API 입니다
//    정렬 기준{sort}과 한 페이지(기본적으로 1 페이지 호출)에 불러 올
//    repository 데이터 갯수를 파라미터 값으로 받습니다
    @GET("/users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user, @Query("sort") String sort,
                               @Query("per_page") int perPage);
}
