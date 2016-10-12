package com.knowledge_seek.phyctogram.retrofitapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import com.knowledge_seek.phyctogram.domain.Users;

/**
 * Created by sjw on 2015-12-29.
 */
public class UsersDes implements JsonDeserializer<Users> {

    @Override
    public Users deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int user_seq = json.getAsJsonObject().get("user_seq").getAsInt();
        String name = json.getAsJsonObject().get("name").getAsString();
        String initials = json.getAsJsonObject().get("initials").getAsString();
        String lifyea = json.getAsJsonObject().get("lifyea").getAsString();
        String mt = json.getAsJsonObject().get("mt").getAsString();
        String de = json.getAsJsonObject().get("de").getAsString();
        String sexdstn = json.getAsJsonObject().get("sexdstn").getAsString();
        int member_seq = json.getAsJsonObject().get("member_seq").getAsInt();

        return new Users(user_seq, name, initials, lifyea, mt, de, sexdstn, member_seq);
    }
}
