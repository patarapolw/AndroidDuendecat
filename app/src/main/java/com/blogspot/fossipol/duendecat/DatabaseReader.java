package com.blogspot.fossipol.duendecat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by patarapolw on 1/15/18.
 */

public class DatabaseReader extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";
    private final Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "active_db";
    private boolean createDb = false, upgradeDb = false;
    private Preferences pref;

    public DatabaseReader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("MainActivity", "Database opening");
        this.context = context;
    }

    private void copyDatabaseFromAssets(SQLiteDatabase db) {
        Log.i(TAG, "copyDatabase");
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            // Open db packaged as asset as the input stream
            myInput = context.getAssets().open("databases/" + pref.getFileName());

            // Open the db in the application package context:
            myOutput = new FileOutputStream(db.getPath());

            // Transfer db file contents:
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            // Set the version of the copied database to the current
            // version:
            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
                    DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(TAG + " Error copying database");
        } finally {
            // Close the streams
            try {
                if (myOutput != null) {
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(TAG + " Error closing streams");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate db");
        createDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade db");
        upgradeDb = true;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "onOpen db");
        pref = new Preferences();
        if (createDb) {// The db in the application package
            // context is being created.
            // So copy the contents from the db
            // file packaged in the assets
            // folder:
            createDb = false;
            copyDatabaseFromAssets(db);

        }
        if (upgradeDb) {// The db in the application package
            // context is being upgraded from a lower to a higher version.
            upgradeDb = false;
            // Your db upgrade logic here:
        }
        copyDatabaseFromAssets(db);
    }

    public Sentence getSentence(int id){
        pref = new Preferences();
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

    public long getNumberOfRows(){
        pref = new Preferences();
        Log.i("MainActivity",pref.getFileName());
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
                result += character;
        }
        return result;
    }

    private class Preferences extends AppCompatActivity {
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

        public String getFileName(){
            if(lang.equals("Chinese"))
                return "chinese.db";
            else
                return "japanese.db";
        }

        public String getSheetName(){
            Random random = new Random();
            if(set.equals("WaniKani")){
                final String[] sheets = {"WK10up", "WK10down"};
                sheetName = sheets[random.nextInt(sheets.length)];
            } else if (set.equals("Core 10k")){
                List<String> sheets = new ArrayList<>();
                sheets.add("Kana");
                for(int i=1; i<=10; i++){
                    sheets.add(String.format("Set%02d", i));
                }
                sheetName = sheets.get(random.nextInt(sheets.size()));
            } else
                sheetName = set;

            return sheetName;
        }
    }

    private int char2int(Character character){
        return character - 'A' + 1;
    }

    private char int2char(Integer integer){
        return (char)(integer - 1 + 'A');
    }
}
