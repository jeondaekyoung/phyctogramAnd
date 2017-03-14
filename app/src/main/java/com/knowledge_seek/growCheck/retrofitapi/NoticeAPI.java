package com.knowledge_seek.growCheck.retrofitapi;

import java.util.List;

import com.knowledge_seek.growCheck.domain.Notice;
import retrofit.Call;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2016-02-15.
 */
public interface NoticeAPI {

    @POST("/notice/list.do")
    Call<List<Notice>> findnoticeList(@Query("pageCnt") int pageCnt);
}
