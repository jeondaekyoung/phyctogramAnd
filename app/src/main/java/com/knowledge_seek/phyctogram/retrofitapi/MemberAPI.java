package com.knowledge_seek.phyctogram.retrofitapi;

import com.knowledge_seek.phyctogram.domain.Member;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sjw on 2015-12-01.
 */
public interface MemberAPI {

    /*@GET("/rest/member/testget")
    public void getFeed(Callback<List<Member>> response);


    @GET("/rest/member/testget")
    Call<Member> getLuxuryMemberGet();

    @FormUrlEncoded
    @POST("/rest/member/testpost")
    Call<Member> getLuxuryMemberPost(@Field("method") String method);

    @FormUrlEncoded
    @POST("/rest/member/testposts")
    Call<List<Member>> getLuxuryMembersPost(@Field("method") String method);

    @FormUrlEncoded
    @POST("/rest/member/testpost1")
    Call<String> testpost1(@Field("member") Member member);


    @FormUrlEncoded
    @POST("/rest/member/testpost2")
    Call<String> testpost2(@Field("member") Member member);

    @GET("/rest/member/testget1")
    Call<String> testget11();*/


    //여기부터 시작
    @POST("/rest/member/register")
    Call<Member> registerMember(@Body Member member);

    @POST("/rest/member/findMemberByMemberSeq")
    Call<Member> findMemberByMemberSeq(@Body int member_seq);

    @POST("/rest/member/findMemberByFacebookInfo")
    Call<Member> findMemberByFacebookInfo(@Body Member member);

    @POST("/rest/member/loginMemberByPhycto")
    Call<Member> loginMemberByPhycto(@Body Member member);

    @DELETE("/rest/member/withdrawMember")
    Call<String> withdrawMember(@Query("member_seq") int member_seq, @Query("pw") String pw,
                                @Query("join_route") String join_route);

    @POST("/rest/member/modifyPwByMember")
    Call<String> modifyPwBymember(@Query("member_seq") int member_seq, @Query("nowpw") String nowpw,
                                  @Query("newpw") String newpw);

    @POST("/rest/member/registerToken")
    Call<String> registerToken(@Query("member_seq") int member_seq, @Query("token") String token);

    @POST("/rest/member/findPw")
    Call<String> findPw(@Query("mailAddr") String mailAddr, @Query("token") String token);
}
