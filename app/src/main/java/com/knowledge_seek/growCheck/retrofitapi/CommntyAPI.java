package com.knowledge_seek.growCheck.retrofitapi;

import com.knowledge_seek.growCheck.domain.Commnty;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2015-12-15.
 */
public interface CommntyAPI {

    @POST("/rest/commnty/registerCommnty")
    Call<String> registerCommnty(@Body Commnty commnty);

    @GET("/rest/commnty/findCommntyByCommntySeq")
    Call<Commnty> findCommntyByCommntySeq(@Query("commnty_seq") int commnty_seq);

}
