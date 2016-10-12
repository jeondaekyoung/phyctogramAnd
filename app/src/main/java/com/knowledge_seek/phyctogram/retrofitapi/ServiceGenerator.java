package com.knowledge_seek.phyctogram.retrofitapi;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import com.knowledge_seek.phyctogram.domain.Analysis;
import com.knowledge_seek.phyctogram.domain.Comment;
import com.knowledge_seek.phyctogram.domain.Commnty;
import com.knowledge_seek.phyctogram.domain.Diary;
import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.domain.Notice;
import com.knowledge_seek.phyctogram.domain.Qa;
import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.domain.Users;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sjw on 2015-12-09.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL =  "http://www.phyctogram.com";

    private static OkHttpClient httpClient = new OkHttpClient();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass){
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, String gubun){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        //구분
        if("Commnty".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Commnty.class, new CommntyDes());
        } else if("Comment".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Comment.class, new CommentDes());
        } else if("Height".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Height.class, new HeightDes());
        } else if("SqlCommntyListView".equals(gubun)){
            gsonBuilder.registerTypeAdapter(SqlCommntyListView.class, new SqlCommntyListViewDes());
        } else if("Users".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Users.class, new UsersDes());
        } else if("Diary".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Diary.class, new DiaryDes());
        } else if("Member".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Member.class, new MemberDes());
        } else if("Analysis".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Analysis.class, new AnalysisDes());
        } else if("Notice".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Notice.class, new NoticeDes());
        } else if("Qa".equals(gubun)){
            gsonBuilder.registerTypeAdapter(Qa.class, new QaDes());
        }

        Gson gson = gsonBuilder.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(serviceClass);

    }
}
