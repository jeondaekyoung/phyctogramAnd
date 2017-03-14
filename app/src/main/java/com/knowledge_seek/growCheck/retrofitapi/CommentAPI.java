package com.knowledge_seek.growCheck.retrofitapi;

import java.util.List;

import com.knowledge_seek.growCheck.domain.Comment;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2015-12-17.
 */
public interface CommentAPI {

    @GET("/rest/comment/findCommentByCommntySeq")
    Call<List<Comment>> findCommentByCommntySeq(@Query("commnty_seq") int commnty_seq);

    @POST("/rest/comment/registerComment")
    Call<String> registerComment(@Body Comment comment);
}
