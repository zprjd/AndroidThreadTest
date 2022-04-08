package com.example.androidthreadtest;

import retrofit2.Call;
import retrofit2.http.POST;

public interface GetRequest_Interface {
    @POST("/")
    Call<Object> postBitMap();

}
