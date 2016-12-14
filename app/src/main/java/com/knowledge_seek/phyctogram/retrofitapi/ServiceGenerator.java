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
        switch (gubun){
            case "Commnty":
                gsonBuilder.registerTypeAdapter(Commnty.class, new CommntyDes());
                break;
            case "Comment":
                gsonBuilder.registerTypeAdapter(Comment.class, new CommentDes());
                break;
            case "Height":
                gsonBuilder.registerTypeAdapter(Height.class, new HeightDes());
                break;
            case "SqlCommntyListView":
                gsonBuilder.registerTypeAdapter(SqlCommntyListView.class, new SqlCommntyListViewDes());
                break;
            case "Users":
                gsonBuilder.registerTypeAdapter(Users.class, new UsersDes());
                break;
            case "Diary":
                gsonBuilder.registerTypeAdapter(Diary.class, new DiaryDes());
                break;
            case "Member":
                gsonBuilder.registerTypeAdapter(Member.class, new MemberDes());
                break;
            case "Analysis":
                gsonBuilder.registerTypeAdapter(Analysis.class, new AnalysisDes());
                break;
            case "Notice":
                gsonBuilder.registerTypeAdapter(Notice.class, new NoticeDes());
                break;
            case "Qa":
                gsonBuilder.registerTypeAdapter(Qa.class, new QaDes());
                break;

        }

        Gson gson = gsonBuilder.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(serviceClass);

    }
}
