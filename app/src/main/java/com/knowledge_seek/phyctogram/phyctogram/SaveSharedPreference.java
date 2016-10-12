package com.knowledge_seek.phyctogram.phyctogram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by sjw on 2015-12-03.
 */
public class SaveSharedPreference {
    static final String PREF_MEMBER_SEQ= "member_seq";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //로그인 정보 저장
    public static void setMemberSeq(Context ctx, String member_seq)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_MEMBER_SEQ, member_seq);
        editor.commit();
        Log.d("-진우-", "preference에 저장함");
    }

    public static String getMemberSeq(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_MEMBER_SEQ, "");
    }

    //로그아웃 정보 삭제
    public static void clearMemberSeq(Context ctx){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
        Log.d("-진우-", "preference를 삭제함");
    }

}
