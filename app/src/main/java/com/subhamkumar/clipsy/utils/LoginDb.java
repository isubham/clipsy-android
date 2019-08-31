package com.subhamkumar.clipsy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LoginDb extends SQLiteOpenHelper {

    private static final String id = "_id";
    private static final String LOGIN_TABLE = "SAVELOGIN";
    private static final String ID = "ID";
    private static final String TOKEN = "TOKEN";
    private static String deleteLoginDetails =  "DELETE FROM " + LOGIN_TABLE;

    private static final String DATABASE_NAME = "CDB";
    private static final int DATABASE_VERSION = 1;


    @Override
    public void onCreate(SQLiteDatabase db) {
        String deleteLoginTable = "DROP TABLE IF EXISTS " + LOGIN_TABLE;
        db.execSQL(deleteLoginTable);
        String createLoginTable = "CREATE TABLE " + LOGIN_TABLE + "(" + id + " INTEGER PRIMARY KEY, " + ID + " TEXT, " + TOKEN + " TEXT)";
        db.execSQL(createLoginTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public LoginDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public long saveLoginDetails(String id, String token) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        //Create a map having movie details to be inserted
        ContentValues loginDetails = new ContentValues();
        loginDetails.put(ID, id);
        loginDetails.put(TOKEN, token);

        long newRowId = db.insert(LOGIN_TABLE, null, loginDetails);
        db.close();
        return newRowId;
    }

    public void deleteLoginDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LOGIN_TABLE + " WHERE "+ id + " > 0");
    }

    public  LoginDetails getLoginDetails() {
        ArrayList<LoginDetails> loginDetail = new ArrayList<>();
        String selectQuery = "SELECT " + id +", " + ID + ", " + TOKEN + " FROM " + LOGIN_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //if TABLE has rows
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                LoginDetails movieDetails = new LoginDetails();
                movieDetails.setId(cursor.getInt(0));
                movieDetails.setID(cursor.getString(1));
                movieDetails.setTOKEN(cursor.getString(2));

                loginDetail.add(movieDetails);
            } while (cursor.moveToNext());
        }
        db.close();
        if (loginDetail.size() > 0) {

            return loginDetail.get(0);
        }
        else{
            return null;
        }
    }

}

