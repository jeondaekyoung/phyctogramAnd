package com.knowledge_seek.growCheck.glowCheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * modify by jdk on 2015-12-03!
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

    //가이드 필요 유무 저장
    public  static void setGuideFlag(Context ctx,boolean bFlag){

        SharedPreferences.Editor editor =getSharedPreferences(ctx).edit();
        editor.putBoolean("guideNeed",bFlag);
        editor.commit();
        Log.d("-대경-", "가이드 필요 유무를 preference에 저장함 :"+getSharedPreferences(ctx).getBoolean("guideNeed",bFlag));
    }
    public  static boolean getGuideFlag(Context ctx){
        return getSharedPreferences(ctx).getBoolean("guideNeed",true);
    }

}
