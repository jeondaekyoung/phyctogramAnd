package com.knowledge_seek.growCheck.retrofitapi;

import com.knowledge_seek.growCheck.domain.Analysis;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sjw on 2016-01-14.
 */
public interface AnalysisAPI {

    @GET("/rest/users/findMonthNumAnimalByUserSeq")
    Call<Analysis> findMonthNumAnimalByUserSeq(@Query("user_seq") int user_seq);

}
