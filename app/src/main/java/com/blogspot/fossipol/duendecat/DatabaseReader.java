package com.blogspot.fossipol.duendecat;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Telephony;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by patarapolw on 1/15/18.
 */

public class DatabaseReader extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";
    private final Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spoonfed";
    private boolean createDb = false, upgradeDb = false;

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
            myInput = context.getAssets().open("databases/spoonfed.db");

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
    }

    public Sentence getSentence(int id){
        Log.i("MainActivity", "Database Started reading");

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor =
                database.rawQuery("SELECT * FROM spoonfed WHERE id=" + id, null);

        Log.i("MainActivity", "Database Started getting grammar");
        Sentence sentence = new Sentence();

        while (cursor.moveToNext()) {
            sentence.setEnglish(cursor.getString(1));
            sentence.setPinyin(cursor.getString(2));
            sentence.setSentence(cursor.getString(3));
        }

        cursor.close();

        return sentence;
    }

    public long getNumberOfRows(){
        SQLiteDatabase database = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(database, "spoonfed");
        database.close();

        return cnt;
    }
}
