package com.knowledge_seek.phyctogram.retrofitapi;

import android.text.format.DateFormat;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.knowledge_seek.phyctogram.domain.Member;

/**
 * Created by sjw on 2015-12-01.
 */
public class MemberDes implements JsonDeserializer<Member> {

    @Override
    public Member deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        long time = Long.parseLong(json.getAsJsonObject().get("join_de").getAsString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String join_de = DateFormat.format("yyyy-MM-dd HH:mm", cal).toString();

        Member member =  new Gson().fromJson(json.getAsJsonObject(), Member.class);
        member.setJoin_de(join_de);
        //Log.d("-진우-", "MemberDes에서 member확인 : " + member.toString());
        return member;
    }
}


/*
public class MemberDes implements JsonDeserializer<Object> {


    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement member = json.getAsJsonObject();
        if(json.getAsJsonObject().get("member") != null){
            member = json.getAsJsonObject().get("member");
        }

        return (new Gson().fromJson( member, Member.class));
    }
}
*/
