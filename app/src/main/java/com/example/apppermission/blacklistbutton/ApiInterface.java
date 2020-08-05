package com.example.apppermission.blacklistbutton;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

        @Headers({
            "Content-Type: application/json",
    })
    @POST("getAllApps")
    Call<BlackListModel> getBlackListApps(@Body JsonArray jsonArray);



}