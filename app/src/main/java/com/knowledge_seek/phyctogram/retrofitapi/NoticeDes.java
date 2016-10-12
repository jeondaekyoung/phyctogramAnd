package com.knowledge_seek.phyctogram.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.knowledge_seek.phyctogram.domain.Notice;

/**
 * Created by sjw on 2016-02-15.
 */
public class NoticeDes implements JsonDeserializer<Notice> {

    @Override
    public Notice deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int notice_seq = json.getAsJsonObject().get("notice_seq").getAsInt();
        String title = json.getAsJsonObject().get("title").getAsString();
        String notice = json.getAsJsonObject().get("notice").getAsString();
        long time = Long.parseLong(json.getAsJsonObject().get("writng_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String writng_de = DateFormat.format("yyyy/MM/dd", cal).toString();
        return new Notice(notice_seq, title, notice, writng_de);
    }
}
