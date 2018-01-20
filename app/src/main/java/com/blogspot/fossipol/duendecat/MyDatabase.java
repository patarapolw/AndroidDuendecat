package com.blogspot.fossipol.duendecat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by patarapolw on 1/21/18.
 */

public class MyDatabase extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION = 1;
    private Preferences pref;
    private final String TAG = "MyDatabase";
    private Context context;

    public MyDatabase(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.context = context;
        this.pref = new Preferences();
    }

    public Sentence getSentence(int id){
        id++;
        Log.i(TAG, "Getting row: " + id);

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor =
                database.rawQuery("SELECT * FROM " + pref.getSheetName() + " WHERE id=" + id, null);

        Sentence sentence = new Sentence();

        while (cursor.moveToNext()) {
            sentence.setSentence(
                    noFurigana(html2text(cursor.getString(cursor.getColumnIndex("lsen")))));
            if(pref.lang.equals("Japanese") && cursor.getColumnIndex("rsen") == -1)
                sentence.setPinyin(html2text(cursor.getString(cursor.getColumnIndex("lsen"))));
            else
                sentence.setPinyin(html2text(cursor.getString(cursor.getColumnIndex("rsen"))));
            sentence.setEnglish(html2text(cursor.getString(cursor.getColumnIndex("esen"))));
        }

        cursor.close();

        return sentence;
    }

    private long getNumberOfRows(){
        SQLiteDatabase database = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(database, pref.getSheetName());
        database.close();

        return cnt;
    }

    public int getRowLimit(){
        long max_row = getNumberOfRows();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("", null);
        int limit = -1;
        int levelCol = -1;
        for(int i=0; i<max_row; i++){
            cursor = database.rawQuery("SELECT * FROM " + pref.getSheetName() + " WHERE id=" + i, null);
            while (cursor.moveToNext()) {
                levelCol = Integer.parseInt(cursor.getString(cursor.getColumnIndex("level")));
            }
            Log.i(TAG,"Level in Col: " + levelCol);
            Log.i(TAG, "Pref lang: " + pref.lang);
            Log.i(TAG,"Pref sheet: " + pref.sheetName);
            Log.i(TAG,"Pref level: " + pref.level);
            if(levelCol > pref.level) {
                limit = i;
                break;
            }
        }
        cursor.close();

        return limit;
    }

    private String html2text(String html){
        return android.text.Html.fromHtml(html).toString();
    }

    private String noFurigana(String sentence){
        String result = "";
        boolean read = true;
        char character;
        for(int i=0; i<sentence.length(); i++){
            character = sentence.charAt(i);
            if(character == '[')
                read = false;
            else if(character == ']')
                read = true;
            else
            if(read)
                result += character;
        }
        return result;
    }

    public class Preferences extends AppCompatActivity {
        private String lang;
        private String set;
        private String sheetName;

        public int level;

        public Preferences(){
            SharedPreferences prefs = context.getSharedPreferences("Config",0);
            lang = prefs.getString("language", "Chinese");
            set = prefs.getString("set", "SpoonFed");
            level = prefs.getInt("level", 5);

            getSheetName();
        }

        /*public String getFileName(){
            if(lang.equals("Chinese"))
                return "chinese.db";
            else
                return "japanese.db";
        }*/

        public String getSheetName(){
            Random random = new Random();
            if(set.equals("WaniKani 10+"))
                sheetName = "WK10up";
            else if(set.equals("WaniKani 10-"))
                sheetName = "WK10down";
            else if (set.equals("Core 10k")){
                List<String> sheets = new ArrayList<>();
                sheets.add("Kana");
                for(int i=1; i<=10; i++){
                    sheets.add(String.format("Set%02d", i));
                }
                sheetName = sheets.get(random.nextInt(sheets.size()));
            }
            else
                sheetName = set;

            return sheetName;
        }
    }
}
