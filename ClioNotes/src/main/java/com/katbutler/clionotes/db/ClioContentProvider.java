package com.katbutler.clionotes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * ClioContentProvider provides content for cached data from Clio
 */
public class ClioContentProvider extends ContentProvider {

    private ClioDatabaseHelper databaseHelper;

    //UriMatcher return codes
    private static final int MATTERS = 10;
    private static final int MATTER_ID = 20;
    private static final int NOTES_REGARDING_MATTER_ID = 30;
    private static final int NOTE_ID_REGARDING_MATTER_ID = 40;

    private static final String AUTHORITY = "com.katbutler.provider.clionotes";
    private static final String MATTER_PATH = "matter";
    private static final String NOTE_PATH = "note";

    //public URI to my data
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MATTER_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH, MATTERS);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/#", MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/#/" + NOTE_PATH, NOTES_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/-1/" + NOTE_PATH, NOTES_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/#/" + NOTE_PATH + "/#", NOTE_ID_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/" + NOTE_PATH + "/#", NOTE_ID_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/" + NOTE_PATH + "/-1", NOTE_ID_REGARDING_MATTER_ID);
    }

    /**
     * Helper mathod to create Uri for Notes path
     * @param matterId
     * @return
     */
    public static Uri getNotesUri(Long matterId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s",AUTHORITY, MATTER_PATH, matterId, NOTE_PATH ));
    }

    public static Uri getNoteUri(Long matterId, Long noteId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s/%d",AUTHORITY, MATTER_PATH, matterId, NOTE_PATH, noteId ));
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new ClioDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MATTERS:
                queryBuilder.setTables(MattersTable.TABLE_MATTER);
                break;
            case MATTER_ID:
                queryBuilder.setTables(MattersTable.TABLE_MATTER);
                // adding the ID to the original query
                queryBuilder.appendWhere(MattersTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case NOTES_REGARDING_MATTER_ID:
                queryBuilder.setTables(NotesTable.TABLE_NOTE);
                queryBuilder.appendWhere(NotesTable.COLUMN_MATTER_ID_FK + "=" + uri.getPathSegments().get(1));
                break;
            case NOTE_ID_REGARDING_MATTER_ID:
                queryBuilder.setTables(NotesTable.TABLE_NOTE);
                queryBuilder.appendWhere(NotesTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int rowsDeleted = 0;


        int uriType = sURIMatcher.match(uri);
        long id = 0;
        Uri returnUri = null;

        switch (uriType) {
            case MATTERS:
                id = sqlDB.replace(MattersTable.TABLE_MATTER, null, values);
                Uri.parse(MATTER_PATH + "/" + id);
                break;
            case MATTER_ID:
                break;
            case NOTES_REGARDING_MATTER_ID:
                values.put(NotesTable.COLUMN_MATTER_ID_FK, uri.getPathSegments().get(1));
                id = sqlDB.replace(NotesTable.TABLE_NOTE, null, values);
                Uri.parse(NOTE_PATH + "/" + id);
                break;
            case NOTE_ID_REGARDING_MATTER_ID:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
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
     * MattersTable represents the SQL table of {@link com.katbutler.clionotes.models.Matter Matters}
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
        public static final String COLUMN_REST_STATE = "rest_state";
        public static final String COLUMN_REST_RESPONSE = "rest_response";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_MATTER
                + "("
                + COLUMN_ID + " integer primary key, "
                + COLUMN_DISPLAY_NUMBER + " text, "
                + COLUMN_STATUS + " text, "
                + COLUMN_DESCRIPTION + " text, "
                + COLUMN_OPEN_DATE + " text, "
                + COLUMN_CLOSE_DATE + " text, "
                + COLUMN_PENDING_DATE + " text, "
                + COLUMN_REST_STATE + " text, "
                + COLUMN_REST_RESPONSE + " text"
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
     * NotesTable represents the SQL table of {@link com.katbutler.clionotes.models.Note Notes}
     */
    public static class NotesTable {

        public static final String TABLE_NOTE = "note";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MATTER_ID_FK = "matter_id_fk";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_DETAIL = "detail";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_REST_STATE = "rest_state";
        public static final String COLUMN_REST_RESPONSE = "rest_response";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NOTE
                + "("
                + COLUMN_ID + " integer primary key, "
                + COLUMN_MATTER_ID_FK + " integer, "
                + COLUMN_SUBJECT + " text, "
                + COLUMN_DETAIL + " text, "
                + COLUMN_DATE + " text, "
                + COLUMN_CREATED_AT + " text, "
                + COLUMN_UPDATED_AT + " text, "
                + COLUMN_REST_STATE + " text, "
                + COLUMN_REST_RESPONSE + " text"
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
