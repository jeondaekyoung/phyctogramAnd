package com.knowledge_seek.phyctogram.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.knowledge_seek.phyctogram.domain.Qa;

/**
 * Created by sjw on 2016-02-16.
 */
public class QaDes implements JsonDeserializer<Qa> {

    @Override
    public Qa deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int qa_seq = json.getAsJsonObject().get("qa_seq").getAsInt();
        String title = json.getAsJsonObject().get("title").getAsString();
        String contents = json.getAsJsonObject().get("contents").getAsString();
        long time = Long.parseLong(json.getAsJsonObject().get("writng_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy/MM/dd", cal).toString();
        String answer = null;
        if(!json.getAsJsonObject().get("answer").isJsonNull()){
            answer = json.getAsJsonObject().get("answer").getAsString();
        }
        int member_seq = json.getAsJsonObject().get("member_seq").getAsInt();
        String state = json.getAsJsonObject().get("state").getAsString();
        return new Qa(qa_seq, title, contents, date, answer, member_seq, state);
    }
}
