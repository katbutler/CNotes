package com.katbutler.clionotes.rest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.models.ClioNote;
import com.katbutler.clionotes.models.Matters;
import com.katbutler.clionotes.models.Notes;

/**
 * {@link RESTProcessor} processes the REST responses recieved from the server.
 *
 */
public class RESTProcessor {

    private Context context;

    public RESTProcessor() {
        context = RESTServiceHelper.getInstance().getContext();
    }


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
        values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.NORMAL);

        Uri uri = ClioContentProvider.getNoteUri(oldNoteId);
        getContentResolver().update(uri, values, ClioContentProvider.NotesTable.COLUMN_ID + "=?", new String[] {oldNoteId.toString()});
    }

    public void processUpdatedNote(Long noteId, ClioNote noteRsp) {
        ContentValues values = new ContentValues();
        values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.NORMAL);

        Uri uri = ClioContentProvider.getNoteUri(noteId);
        getContentResolver().update(uri, values, ClioContentProvider.NotesTable.COLUMN_ID+ "=?", new String[] {noteId.toString()});
    }

    /**
     * Helper method to get the Content Resolver from the context
     * @return
     */
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }
}
