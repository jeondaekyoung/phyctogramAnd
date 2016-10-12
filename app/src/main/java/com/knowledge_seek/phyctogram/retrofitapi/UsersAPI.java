package com.knowledge_seek.phyctogram.retrofitapi;

import java.util.List;

import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Users;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2015-12-07.
 */
public interface UsersAPI {

    @POST("/rest/users/register")
    Call<String> registerUsers(@Body Users users);

    @GET("/rest/users/findUsersByMemberSeq")
    Call<List<Users>> findUsersByMember(@Query("member_seq") String member_seq);

    @POST("/rest/users/delUsersByUserSeq")
    Call<String> delUsersByUserSeq(@Query("user_seq") String user_seq);

    @POST("/rest/users/modUsersByUsers")
    Call<String> modUsersByUsers(@Body Users users);

    @GET("/rest/users/findUsersMainInfoByUserSeq")
    Call<List<Height>> findUsersMainInfoByUserSeq(@Query("user_seq") int user_seq);

    @GET("/rest/users/findUsersAnalysisByUserSeq")
    Call<List<Height>> findUsersAnalysisByUserSeq(@Query("user_seq") int user_seq);

}
