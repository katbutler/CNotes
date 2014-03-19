package com.katbutler.clionotes.rest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.db.ClioDatabaseQueryHelper;
import com.katbutler.clionotes.models.ClioNote;
import com.katbutler.clionotes.models.Matters;
import com.katbutler.clionotes.models.Note;
import com.katbutler.clionotes.models.Notes;

/**
 * {@link RESTProcessor} processes the REST responses recieved from the server.
 *
 */
public class RESTProcessor {

    /**
     * The Context to run commands on
     */
    private Context context;

    /**
     * Create a {@link RESTProcessor} that uses the same context as the RESTServiceHelper
     */
    public RESTProcessor() {
        context = RESTServiceHelper.getInstance().getContext();
    }


    /**
     * Process the GET request for all {@link com.katbutler.clionotes.models.Matter Matters} for this user.
     * @param matters
     */
    public void processMatters(Matters matters) {
//
//        for (Matter matter : matters.getMatters()) {
//            Uri matterUri = Uri.parse(ClioContentProvider.CONTENT_URI + "/" + matter.getId());
//
//            if (getContentResolver().update(ClioContentProvider.CONTENT_URI, values, null, null) == 0) {
//                getContentResolver().insert(ClioContentProvider.CONTENT_URI, values);
//            }
//        }

        getContentResolver().bulkInsert( ClioContentProvider.CONTENT_URI, matters.getContentValues().toArray( new ContentValues[0] ) );
    }

    /**
     * Process the GET request for all {@link Note Notes} regarding a {@link com.katbutler.clionotes.models.Matter}
     * @param notes
     * @param matterId
     */
    public void processNotesForMatter(Notes notes, Long matterId) {
        getContentResolver().bulkInsert( ClioContentProvider.getNotesUri(matterId), notes.getContentValues().toArray( new ContentValues[0] ) );
    }


    /**
     * Process the newly created note.
     * @param oldNoteId
     * @param noteRsp
     */
    public void processCreatedNote(Long oldNoteId, ClioNote noteRsp) {

        ContentValues values = new ContentValues();
        values.put(ClioContentProvider.NotesTable.COLUMN_ID, noteRsp.getNote().getId());
        values.put(ClioContentProvider.NotesTable.COLUMN_SUBJECT, noteRsp.getNote().getSubject());
        values.put(ClioContentProvider.NotesTable.COLUMN_DETAIL, noteRsp.getNote().getDetail());
        values.put(ClioContentProvider.NotesTable.COLUMN_DATE, noteRsp.getNote().getDate());
        values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.NORMAL);

        Long matterId = noteRsp.getNote().getRegarding().getId();

        Uri uri = ClioContentProvider.getNoteRegardMatterUri(matterId, noteRsp.getNote().getId());
        getContentResolver().update(uri, values, ClioContentProvider.NotesTable.COLUMN_ID + "=?", new String[] {oldNoteId.toString()});
    }

    /**
     * Process the Clio update note response.
     * @param noteId
     * @param noteRsp
     */
    public void processUpdatedNote(Long noteId, ClioNote noteRsp) {
        ContentValues values = new ContentValues();
        values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.NORMAL);
        Long matterId = noteRsp.getNote().getRegarding().getId();

        Uri uri = ClioContentProvider.getNoteRegardMatterUri(matterId, noteId);
        getContentResolver().update(uri, values, ClioContentProvider.NotesTable.COLUMN_ID+ "=?", new String[] {noteId.toString()});
    }

    /**
     * Process the deleted note
     * @param noteId
     */
    public void processDeletedNote(Long noteId) {
        Note note = ClioDatabaseQueryHelper.getNoteWithId(getContentResolver(), noteId);
        Uri uri = ClioContentProvider.getNoteRegardMatterForceUri(note.getRegarding().getId(), noteId);
        getContentResolver().delete(uri, ClioContentProvider.NotesTable.COLUMN_ID + "=?", new String[]{noteId.toString()});
    }

    /**
     * Helper method to get the Content Resolver from the context
     * @return
     */
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }
}
