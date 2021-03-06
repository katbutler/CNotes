package com.katbutler.clionotes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.katbutler.clionotes.models.Matter;
import com.katbutler.clionotes.rest.RESTConstants;
import com.katbutler.clionotes.rest.RESTServiceHelper;

import java.util.Date;

/**
 * ClioContentProvider provides content for cached data from Clio
 */
public class ClioContentProvider extends ContentProvider {

    private ClioDatabaseHelper databaseHelper;

    //UriMatcher return codes
    private static final int MATTERS = 10;
    private static final int MATTER_ID = 20;
    private static final int NOTES_REGARDING_MATTER_ID = 30;
    private static final int NOTE_ID = 40;
    private static final int NOTE_ID_REGARDING_MATTER_ID = 41;
    private static final int NOTE_FORCE_ID = 50;
    private static final int NOTE_ID_REGARDING_MATTER_ID_FORCE = 51;


    private static final String AUTHORITY = "com.katbutler.provider.clionotes";
    private static final String MATTER_PATH = "matter";
    private static final String NOTE_PATH = "note";
    private static final String FORCE_PATH = "force";

    //public URI to my data
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MATTER_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH, MATTERS);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/*", MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/*/" + NOTE_PATH, NOTES_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/*/" + NOTE_PATH + "/*", NOTE_ID_REGARDING_MATTER_ID);
        sURIMatcher.addURI(AUTHORITY, MATTER_PATH + "/*/" + NOTE_PATH + "/*/" + FORCE_PATH, NOTE_ID_REGARDING_MATTER_ID_FORCE);
        sURIMatcher.addURI(AUTHORITY, NOTE_PATH + "/*", NOTE_ID);
        sURIMatcher.addURI(AUTHORITY, NOTE_PATH + "/*/" + FORCE_PATH, NOTE_FORCE_ID);
    }

    /**
     * Helper method to create Uri for Notes path
     * /matter/#/note
     * GET ALL NOTES FOR MATTER, CREATE NOTE
     *
     * @param matterId
     * @return
     */
    public static Uri getNotesUri(Long matterId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s",AUTHORITY, MATTER_PATH, matterId, NOTE_PATH ));
    }

    /**
     * Helper method to create Uri for single note path
     * /note/#
     * FAKE DELETE, GET NOTE, UPDATE NOTE
     *
     * @param noteId
     * @return
     */
    public static Uri getNoteUri(Long noteId) {
        return Uri.parse(String.format("content://%s/%s/%d",AUTHORITY, NOTE_PATH, noteId ));
    }

    /**
     * Helper method to create Uri for deleting after response from clio
     * /note/#/force
     * DELETE FORCED
     *
     * @param noteId
     * @return
     */
    public static Uri getNoteForceUri(Long noteId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s",AUTHORITY, NOTE_PATH, noteId, FORCE_PATH ));
    }

    /**
     * Helper method to create Uri for single note path
     * matter/#/note/#
     * FAKE DELETE, GET NOTE, UPDATE NOTE
     *
     * @param noteId
     * @return
     */
    public static Uri getNoteRegardMatterUri(Long matterId, Long noteId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s/%d",AUTHORITY, MATTER_PATH, matterId, NOTE_PATH, noteId ));
    }

    /**
     * Helper method to create Uri for deleting after response from clio
     * matter/#/note/#/force
     * DELETE FORCED
     *
     * @param noteId
     * @return
     */
    public static Uri getNoteRegardMatterForceUri(Long matterId, Long noteId) {
        return Uri.parse(String.format("content://%s/%s/%d/%s/%d/%s",AUTHORITY, MATTER_PATH, matterId, NOTE_PATH, noteId, FORCE_PATH ));
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
                queryBuilder.appendWhere(") AND ((");
                // (rest_state ISNULL) OR (rest_state NOT LIKE 'DELETING');
                queryBuilder.appendWhere(NotesTable.COLUMN_REST_STATE + " ISNULL) OR (");
                queryBuilder.appendWhere(NotesTable.COLUMN_REST_STATE + " NOT LIKE '" + RESTConstants.RESTStates.DELETING + "')"); // Must add so we do not see the ones currently being deleted
                break;
            case NOTE_ID:
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
            case NOTES_REGARDING_MATTER_ID: // GET/POST
                values.put(NotesTable.COLUMN_MATTER_ID_FK, uri.getPathSegments().get(1));
                id = sqlDB.replace(NotesTable.TABLE_NOTE, null, values);
                Uri.parse(NOTE_PATH + "/" + id);

                if(values.containsKey(NotesTable.COLUMN_REST_STATE)) {
                    if(values.getAsString(NotesTable.COLUMN_REST_STATE).equals(RESTConstants.RESTStates.POSTING)) {
                        RESTServiceHelper.getInstance().createNote(Long.parseLong(uri.getPathSegments().get(1)), values.getAsLong(NotesTable.COLUMN_ID));
                    }
                }

                break;
            case NOTE_ID: // GET/PUT
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {

        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        int numRows = 0;
        Long matterId = null;

        switch (uriType) {
            /*
            This is a 'fake' delete. It changes the REST state to Deleting
            so we know that it needs to be deleted from Clio. Once we get
            the response from Clio that it has been deleted, we can safely
            delete it from our local database.
             */
            case NOTE_ID_REGARDING_MATTER_ID:
                matterId = Long.parseLong(uri.getPathSegments().get(1));
            case NOTE_ID: // FAKE DELETE
                ContentValues values = new ContentValues();
                values.put(NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.DELETING);
                numRows = sqlDB.update(NotesTable.TABLE_NOTE, values, NotesTable.COLUMN_ID + " = ?", new String[] {uri.getLastPathSegment()});

                RESTServiceHelper.getInstance().deleteNote(Long.parseLong(uri.getLastPathSegment()));
                break;
            case NOTE_ID_REGARDING_MATTER_ID_FORCE:
                matterId = Long.parseLong(uri.getPathSegments().get(1));
                numRows = sqlDB.delete(NotesTable.TABLE_NOTE, NotesTable.COLUMN_ID + " = ?", new String[] {uri.getPathSegments().get(3)});
                break;
            case NOTE_FORCE_ID: // REAL DELETE
                numRows = sqlDB.delete(NotesTable.TABLE_NOTE, NotesTable.COLUMN_ID + " = ?", new String[] {uri.getPathSegments().get(1)});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        if (matterId == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Uri uriWithMatter = getNotesUri(matterId);
            getContext().getContentResolver().notifyChange(uriWithMatter, null);
        }

        return numRows;

    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int rowsDeleted = 0;


        int uriType = sURIMatcher.match(uri);
        long id = 0;
        Long matterID = null;
        Uri returnUri = null;

        switch (uriType) {
            case MATTERS:
                id = sqlDB.replace(MattersTable.TABLE_MATTER, null, values);
                Uri.parse(MATTER_PATH + "/" + id);
                break;
            case MATTER_ID:
                break;
            case NOTES_REGARDING_MATTER_ID: // GET/POST   matter/#/note
                break;
            case NOTE_ID_REGARDING_MATTER_ID: // GET/PUT  /note/#
                // Use the URI scheme when we need the matter id
                matterID = Long.parseLong(uri.getPathSegments().get(1));
            case NOTE_ID:
                id = sqlDB.update(NotesTable.TABLE_NOTE, values, where, whereArgs);

                if(values.containsKey(NotesTable.COLUMN_REST_STATE)) {
                    if(values.getAsString(NotesTable.COLUMN_REST_STATE).equals(RESTConstants.RESTStates.PUTING)) {
                        RESTServiceHelper.getInstance().updateNote(values.getAsLong(NotesTable.COLUMN_ID));
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (matterID == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Uri uriWithMatter = getNotesUri(matterID);
            getContext().getContentResolver().notifyChange(uriWithMatter, null);
        }

        return 1;
    }


    /**
     * Create a new unique id for new object before they are posted to Clio server
     * @return
     */
    public static long createNewIndex() {
        return (new Date().getTime() * -1);
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
