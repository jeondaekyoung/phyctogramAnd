package com.knowledge_seek.phyctogram.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.knowledge_seek.phyctogram.domain.Comment;

/**
 * Created by sjw on 2015-12-17.
 */
public class CommentDes implements JsonDeserializer{

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int comment_seq = json.getAsJsonObject().get("comment_seq").getAsInt();
        String content = json.getAsJsonObject().get("content").getAsString();
        long time = Long.parseLong(json.getAsJsonObject().get("writng_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String writng_de = DateFormat.format("MM/dd", cal).toString();
        int member_seq = json.getAsJsonObject().get("member_seq").getAsInt();
        String member_name = json.getAsJsonObject().get("member_name").getAsString();
        int commnty_seq = json.getAsJsonObject().get("commnty_seq").getAsInt();
        return new Comment(comment_seq, content, writng_de, member_seq, member_name, commnty_seq);
    }
}
