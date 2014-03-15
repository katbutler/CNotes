package com.katbutler.clionotes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ClioDatabaseHelper
 */
public class ClioDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clio.db";
    private static final int DATABASE_VERSION = 1;


    public ClioDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ClioContentProvider.MattersTable.onCreate(sqLiteDatabase);
        ClioContentProvider.NotesTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int ver1, int ver2) {
        ClioContentProvider.MattersTable.onUpgrade(sqLiteDatabase, ver1, ver2);
        ClioContentProvider.NotesTable.onUpgrade(sqLiteDatabase, ver1, ver2);
    }
}
