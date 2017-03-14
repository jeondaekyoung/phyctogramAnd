package com.knowledge_seek.growCheck.retrofitapi;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by sjw on 2016-02-24.
 */
public interface FileUploadService {



    @Multipart
    @POST("/fileUpload/single")
    Call<String> fileUploadSingle(@Part("diary_seq") int diary_seq,
            @Part("myfile\"; filename=\"phyctogram.jpg") RequestBody file);

    @Multipart
    @POST("/fileUpload/single1")
    Call<String> upload1(@Part("myfile\"; filename=\"phyctogram.jpg") RequestBody file);

    /*@Multipart
    @POST("/fileUpload/single0")
    Call<String> upload0(@Part("myfile\"; filename=\"phyctogram.jpg\" ") RequestBody file,
                         @Part("description") RequestBody description);

    @POST("/fileUpload/single2")
    Call<String> upload2(@Body RequestBody file);

    @Multipart
    @POST("/fileUpload/single42")
    Call<String> upload3(@Body Diary diary,
                         @Part("myfile\"; filename=\"phyctogram.jpg\" ") RequestBody file);

    @Multipart
    @POST("/fileUpload/single43")
    Call<String> upload43(@Body Diary diary);*/
}
