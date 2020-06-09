package com.example.carcarcarcar;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    String BASE_URL = Config.getUrl("");


    @Multipart
    @POST("/upload")
    Call<ResponseBody> uploadMultiple(
            @Part List<MultipartBody.Part> car_photos
    );

    @Multipart
    @POST("/python/yolo")
    Call<ResponseBody> requestYOLO(

            @Part("rent_id") RequestBody rent_id,
            @Part("before_after") RequestBody state,
            @Part("YOLO_request") RequestBody YOLO_request
    );


}
