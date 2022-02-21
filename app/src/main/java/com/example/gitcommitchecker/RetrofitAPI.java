package com.example.gitcommitchecker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user, @Query("sort") String sort,
                               @Query("per_page") int perPage);
}
