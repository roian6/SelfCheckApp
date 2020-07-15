package com.david0926.selfcheck.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("/stv_cvd_co00_004.do")
    Call<ResponseBody> postSchool(@Body RequestBody body);

    @POST("/stv_cvd_co00_003.do")
    Call<ResponseBody> postSearch(@Body RequestBody body);

    @POST("/stv_cvd_co00_012.do")
    Call<ResponseBody> postInfo(@Body RequestBody body);

    @POST("/stv_cvd_co01_000.do")
    Call<ResponseBody> postCheck(@Body RequestBody body);

    @POST("/stv_cvd_co02_000.do")
    Call<ResponseBody> postResult(@Body RequestBody body);

}
