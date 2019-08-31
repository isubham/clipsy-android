package com.subhamkumar.clipsy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.subhamkumar.clipsy.models.Constants;

public class LoginPersistance {

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static void Save(String id, String token, Context context) {
        SharedPreferences sp = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.TOKEN, token);
        editor.putString(Constants.id, id);
        editor.commit();
        editor.apply();
    }

   public static void Delete(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static String GetToken(Context context) {
         SharedPreferences sp = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
         return sp.getString(Constants.TOKEN, null);
    }

    public static String GetId(Context context) {
         SharedPreferences sp = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
         return sp.getString(Constants.id, null);
    }



}
