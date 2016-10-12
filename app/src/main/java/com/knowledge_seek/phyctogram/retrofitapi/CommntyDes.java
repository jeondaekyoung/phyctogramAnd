package com.knowledge_seek.phyctogram.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import java.util.Calendar;

import com.knowledge_seek.phyctogram.domain.Commnty;

/**
 * Created by sjw on 2015-12-15.
 */
public class CommntyDes implements JsonDeserializer<Commnty> {

    //final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Commnty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int commnty_seq = json.getAsJsonObject().get("commnty_seq").getAsInt();
        String title = json.getAsJsonObject().get("title").getAsString();
        String contents = json.getAsJsonObject().get("contents").getAsString();
        long time = Long.parseLong(json.getAsJsonObject().get("writng_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        int hits_co = json.getAsJsonObject().get("hits_co").getAsInt();
        int member_seq = json.getAsJsonObject().get("member_seq").getAsInt();
        return new Commnty(commnty_seq, title, contents, date, hits_co, member_seq);
    }
}
