package com.example.gitcommitchecker;

import com.google.gson.annotations.SerializedName;

public class Repo {
    @SerializedName("pushed_at")
    private String pushedAt;

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }
}
