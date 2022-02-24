package com.example.gitcommitchecker;

import com.google.gson.annotations.SerializedName;

//    GitHub API 에서 호출할 repository 데이터의 DTO 클래스입니다
public class Repo {
//    repository 의 가장 최근 커밋 날짜 정보
    @SerializedName("pushed_at")
    private String pushedAt;

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }
}
