package com.knowledge_seek.phyctogram.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.knowledge_seek.phyctogram.domain.Comment;
import com.knowledge_seek.phyctogram.domain.Commnty;
import com.knowledge_seek.phyctogram.domain.Diary;
import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.domain.Qa;
import com.knowledge_seek.phyctogram.domain.Users;

/**
 * Created by sjw on 2015-11-30.
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;

    //email pattern
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }
    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    //날짜가 한자리일때 앞에 0을 붙이자.
    public static String dateFormat(int x){
        String s = String.valueOf(x);
        if(s.length() == 1){
            s = "0".concat(s);
        }
        return s;
    }

    public static synchronized void seqChange(List<Users> users, int user_seq) {

        Log.d("-진우-", user_seq + " 선택한 내 아이 번호입니다");

        //Log.d("-진우-", "=========================================");
        for(int i=0; i<users.size() ;i++){
            Log.d("-진우-", (i+1) + " 번째 : " + users.get(i));
        }

        int index = 0;
        for(int i=0; i<users.size(); i++) {
            if(users.get(i).getUser_seq() == user_seq) {
                index = i;
                break;
            }
        }

        users.add(users.get(index));
        users.remove(index);

        Log.d("-진우-", "=========================================");
        for(int i=0; i<users.size() ;i++){
            Log.d("-진우-", (i+1) + " 번째 : " + users.get(i));
        }

    }

    public static synchronized void compareList(List<Users> usersList, List<Users> usersTask) {

        boolean same = false;
        List<Users> newUser = new ArrayList<Users>();
        for(Users ut : usersTask) {
            //Log.d("-진우-", "교체할 것 " + ut.toString());
            for(Users ul : usersList) {
                //Log.d("-진우-", "교체전 " + ul.toString());
                if(ut.getUser_seq() == ul.getUser_seq()) {
                    same = true;
                }

            }
            if(same == false) {
                newUser.add(ut);        //추가
                Log.d("-내 아이 목록 변경- ", "추가 : " + ut.toString());
            }
            same = false;
        }
        for(Users add : newUser) {
            usersList.add(add);
        }

        newUser.clear();
        same = false;
        for(Users ul : usersList) {
            for(Users ut : usersTask) {
                if(ut.getUser_seq() == ul.getUser_seq()) {
                    same = true;
                }
            }
            if(same == false) {
                newUser.add(ul);        //삭제
                Log.d("-내 아이 목록 변경- ", "삭제 : " + ul.toString());
            }
            same = false;
        }
        for(Users rm : newUser) {
            usersList.remove(rm);

        }
    }

    public static String member2json(Member member) {
        return new Gson().toJson(member);
    }

    public static String users2json(Users users) {
        return new Gson().toJson(users);
    }

    public static String comment2json(Comment comment) {
        return new Gson().toJson(comment);
    }

    public static String qa2json(Qa qa) {
        return new Gson().toJson(qa);
    }

    public static String commnty2json(Commnty commnty){
        return new Gson().toJson(commnty);
    }

    public static String height2json(Height height){
        return new Gson().toJson(height);
    }

    public static String diary2json(Diary diary) {
        return new Gson().toJson(diary);
    }

    public static File saveBitmaptoJpeg(Bitmap bitmap) {
        String folder_name = "/phyctogram/";
        String file_name = "phyctogram.jpg";
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath() + folder_name;

        File file_path = new File(ex_storage);
        if(!file_path.isDirectory()) {
            file_path.mkdirs();
        }

        File jpgfile = new File(ex_storage+file_name);
        OutputStream out = null;
        try {
            jpgfile.createNewFile();
            out = new FileOutputStream(jpgfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("-진우-", "저장위치 : " + ex_storage + file_name);
        return new File(ex_storage+file_name);

    }

    public static String saveBitmaptoJpegPath(Bitmap bitmap) {
        String folder_name = "/phyctogram/";
        String file_name = "phyctogram.jpg";
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath() + folder_name;

        File file_path = new File(ex_storage);
        if(!file_path.isDirectory()) {
            file_path.mkdirs();
        }

        File jpgfile = new File(ex_storage+file_name);
        OutputStream out = null;
        try {
            jpgfile.createNewFile();
            out = new FileOutputStream(jpgfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("-진우-", "저장위치 : " + ex_storage + file_name);
        return ex_storage+file_name;

    }
}
