package com.katbutler.clionotes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class to manage local clio database creaation and version management.
 */
public class ClioDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clio.db";
    private static final int DATABASE_VERSION = 1;


    /**
     * Constructor to intialize the {@link android.database.sqlite.SQLiteOpenHelper}
     * with the {@link android.content.Context} and DB name/version
     * @param context
     */
    public ClioDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Create local Clio database schema
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ClioContentProvider.MattersTable.onCreate(sqLiteDatabase);
        ClioContentProvider.NotesTable.onCreate(sqLiteDatabase);
    }

    /**
     * Migrate local Clio database schema
     * @param sqLiteDatabase
     * @param ver1
     * @param ver2
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int ver1, int ver2) {
        ClioContentProvider.MattersTable.onUpgrade(sqLiteDatabase, ver1, ver2);
        ClioContentProvider.NotesTable.onUpgrade(sqLiteDatabase, ver1, ver2);
    }
}
