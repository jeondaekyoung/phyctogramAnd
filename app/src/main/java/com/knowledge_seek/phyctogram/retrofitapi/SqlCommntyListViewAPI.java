package com.knowledge_seek.phyctogram.retrofitapi;

import java.util.List;

import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sjw on 2015-12-15.
 */
public interface SqlCommntyListViewAPI {

    @GET("/rest/commnty/findCommntyLatest")
    Call<List<SqlCommntyListView>> findCommntyLatest(@Query("pageCnt") int pageCnt);

    @GET("/rest/commnty/findCommntyPopular")
    Call<List<SqlCommntyListView>> findCommntyPopular(@Query("pageCnt") int pageCnt);

    @GET("/rest/commnty/findCommntyPopularTop3")
    Call<List<SqlCommntyListView>> findCommntyPopularTop3();

}
