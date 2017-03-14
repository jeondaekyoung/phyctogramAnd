package com.knowledge_seek.growCheck.retrofitapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import com.knowledge_seek.growCheck.domain.Diary;

/**
 * Created by sjw on 2015-12-29.
 */
public class DiaryDes implements JsonDeserializer<Diary> {

    @Override
    public Diary deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int diary_seq = json.getAsJsonObject().get("diary_seq").getAsInt();
        String title = json.getAsJsonObject().get("title").getAsString();
        String contents = json.getAsJsonObject().get("contents").getAsString();
        String writng_year = json.getAsJsonObject().get("writng_year").getAsString();
        String writng_mt = json.getAsJsonObject().get("writng_mt").getAsString();
        String writng_de = json.getAsJsonObject().get("writng_de").getAsString();
        int user_seq = json.getAsJsonObject().get("user_seq").getAsInt();
        return new Diary(diary_seq, title, contents, writng_year, writng_mt, writng_de, user_seq);
    }
}
