package com.example.speechbuddy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WordApi {

    @GET("word")
    Call<List<String>> getRandomWord();
}
