package com.knowledge_seek.growCheck.retrofitapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import com.knowledge_seek.growCheck.domain.Analysis;

/**
 * Created by sjw on 2016-01-14.
 */
public class AnalysisDes implements JsonDeserializer<Analysis> {

    @Override
    public Analysis deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int analysis_seq = json.getAsJsonObject().get("analysis_seq").getAsInt();
        String sexdstn = json.getAsJsonObject().get("sexdstn").getAsString();
        int month_num = json.getAsJsonObject().get("month_num").getAsInt();
        double height = json.getAsJsonObject().get("height").getAsDouble();
        int rank = json.getAsJsonObject().get("rank").getAsInt();
        String animal_img = json.getAsJsonObject().get("animal_img").getAsString();

        return new Analysis(analysis_seq, sexdstn, month_num, height, rank, animal_img);
    }
}
