package com.kevinbaguisa.motionsensor2.Controller;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RESTAPI {

    @FormUrlEncoded
    @POST("accelerometer/")
    Call<ResponseBody> postAcceAvg(
            @Field("averageX") float averageX,
            @Field("averageY") float averageY,
            @Field("averageZ") float averageZ
    );

    @FormUrlEncoded
    @POST("gyroscope/")
    Call<ResponseBody> postGyroAvg(
            @Field("averageX") float averageX,
            @Field("averageY") float averageY,
            @Field("averageZ") float averageZ
    );
}