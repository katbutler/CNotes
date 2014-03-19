package com.katbutler.clionotes.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.katbutler.clionotes.models.Note;

public class ClioDatabaseQueryHelper {

    /**
     * Get the note from {@link com.katbutler.clionotes.db.ClioContentProvider} with noteId
     * @param noteId The ID of the note we are looking for
     * @return
     */
    public static Note getNoteWithId(ContentResolver contentResolver, Long noteId) {

        Note note = null;

        String[] projection = new String[] {ClioContentProvider.NotesTable.COLUMN_ID,
                ClioContentProvider.NotesTable.COLUMN_SUBJECT,
                ClioContentProvider.NotesTable.COLUMN_DETAIL,
                ClioContentProvider.NotesTable.COLUMN_DATE,
                ClioContentProvider.NotesTable.COLUMN_MATTER_ID_FK};

        Uri uri = ClioContentProvider.getNoteUri(noteId);

        Cursor cursor = contentResolver.query(uri, projection, null, null, ClioContentProvider.NotesTable.COLUMN_ID + " ASC LIMIT 1");

        if (cursor.moveToFirst()) {
            note = new Note();
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_ID));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_SUBJECT));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DETAIL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DATE));
            Long matterId = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_MATTER_ID_FK));

            note.setId(id);
            note.setSubject(subject);
            note.setDetail(detail);
            note.setDate(date);
            note.setRegarding(note.new Regarding(matterId));
        }

        cursor.close();

        return note;

    }


}
