package com.knowledge_seek.growCheck.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.knowledge_seek.growCheck.domain.SqlCommntyListView;

/**
 * Created by sjw on 2015-12-15.
 */
public class SqlCommntyListViewDes implements JsonDeserializer<SqlCommntyListView> {

    //final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public SqlCommntyListView deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int commnty_seq = json.getAsJsonObject().get("commnty_seq").getAsInt();
        String title = json.getAsJsonObject().get("title").getAsString();
        String name = json.getAsJsonObject().get("name").getAsString();
        long time = Long.parseLong(json.getAsJsonObject().get("writng_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String writng_de = DateFormat.format("MM/dd", cal).toString();
        int hits_co = json.getAsJsonObject().get("hits_co").getAsInt();
        int cnt = json.getAsJsonObject().get("cnt").getAsInt();

        return new SqlCommntyListView(commnty_seq, title, name, writng_de, hits_co, cnt);
    }
}
