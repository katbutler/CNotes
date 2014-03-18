package com.katbutler.clionotes.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.katbutler.clionotes.models.Note;

public class ClioDatabaseQueryHelper {

    /**
     * Get the note from {@link com.katbutler.clionotes.db.ClioContentProvider} with noteId
     * @param matterId
     * @param noteId
     * @return
     */
    public static Note getNoteWithId(ContentResolver contentResolver, Long matterId, Long noteId) {

        Note note = null;

        String[] projection = new String[] {ClioContentProvider.NotesTable.COLUMN_ID,
                ClioContentProvider.NotesTable.COLUMN_SUBJECT,
                ClioContentProvider.NotesTable.COLUMN_DETAIL};

        Uri uri = ClioContentProvider.getNoteUri(noteId);

        Cursor cursor = contentResolver.query(uri, projection, null, null, ClioContentProvider.NotesTable.COLUMN_ID + " ASC LIMIT 1");

        if (cursor.moveToFirst()) {
            note = new Note();
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_ID));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_SUBJECT));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DETAIL));

            note.setId(id);
            note.setSubject(subject);
            note.setDetail(detail);
            note.setRegarding(note.new Regarding(matterId));
        }

        return note;

    }


}
