package com.katbutler.clionotes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * ClioContentProvider provides content for cached data from Clio
 */
public class ClioContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    /**
     * MattersTable represents the SQL table of {@link Matter Matters}
     */
    public static class MattersTable {
        public static final String TABLE_MATTER = "matter";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DISPLAY_NUMBER = "display_number";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_OPEN_DATE = "open_date";
        public static final String COLUMN_CLOSE_DATE = "close_date";
        public static final String COLUMN_PENDING_DATE = "pending_date";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_MATTER
                + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_DISPLAY_NUMBER + " text, "
                + COLUMN_STATUS + " text,"
                + COLUMN_DESCRIPTION + " text"
                + COLUMN_OPEN_DATE + " text,"
                + COLUMN_CLOSE_DATE + " text,"
                + COLUMN_PENDING_DATE + " text"
                + ");";


        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }


        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(MattersTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATTER);
            onCreate(database);
        }
    }




    /**
     * NotesTable represents the SQL table of {@link Note Notes}
     */
    public static class NotesTable {

        public static final String TABLE_NOTE = "note";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_DETAIL = "detail";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NOTE
                + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_SUBJECT + " text, "
                + COLUMN_DETAIL + " text,"
                + COLUMN_DATE + " text"
                + COLUMN_CREATED_AT + " text,"
                + COLUMN_UPDATED_AT + " text"
                + ");";


        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }


        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(MattersTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
            onCreate(database);
        }


    }

}
