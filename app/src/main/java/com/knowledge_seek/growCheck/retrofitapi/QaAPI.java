package com.knowledge_seek.growCheck.retrofitapi;

import java.util.List;

import com.knowledge_seek.growCheck.domain.Qa;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2016-02-16.
 */
public interface QaAPI {

    @POST("/rest/qa/findqaByMemberSeq")
    Call<List<Qa>> findqaByMemberSeq(@Query("member_seq") int member_seq,
                                     @Query("pageCnt") int pageCnt);

    @POST("/rest/qa/registerQa")
    Call<String> registerQa(@Body Qa qa);
}
