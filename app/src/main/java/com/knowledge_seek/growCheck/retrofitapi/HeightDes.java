package com.knowledge_seek.growCheck.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.knowledge_seek.growCheck.domain.Height;

/**
 * Created by sjw on 2015-12-14.
 */
public class HeightDes implements JsonDeserializer<Height> {

    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Height deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String height_seq = json.getAsJsonObject().get("height_seq").getAsString();
        double height = json.getAsJsonObject().get("height").getAsDouble();
        String mesure_date = json.getAsJsonObject().get("mesure_date").getAsString();
        int user_seq = json.getAsJsonObject().get("user_seq").getAsInt();
        long time = Long.parseLong(json.getAsJsonObject().get("input_date").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        int rank = json.getAsJsonObject().get("rank").getAsInt();
        double height_50 = json.getAsJsonObject().get("height_50").getAsDouble();
        String animal_img = null;
        if(!json.getAsJsonObject().get("animal_img").isJsonNull()){
            animal_img = json.getAsJsonObject().get("animal_img").getAsString();
        }
        return new Height(height_seq, height, mesure_date, user_seq, date, rank, height_50, animal_img);
    }
}
