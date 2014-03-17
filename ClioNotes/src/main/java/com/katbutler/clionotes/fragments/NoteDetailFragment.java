package com.katbutler.clionotes.fragments;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.katbutler.clionotes.R;
import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.models.Note;
import com.katbutler.clionotes.rest.RESTConstants;

import java.net.URI;

/**
 * NoteDetailFragment used to edit/create {@link com.katbutler.clionotes.models.Note} details
 */
public class NoteDetailFragment extends Fragment {

    private Long matterId = -1l;
    private Long noteId = -1l;


    private EditText subjectEditText;
    private EditText detailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO initialize fragment
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from intents
        if (getArguments().containsKey(Constants.IntentExtrasKeys.MATTER_ID))
            matterId = getArguments().getLong(Constants.IntentExtrasKeys.MATTER_ID);

        if (getArguments().containsKey(Constants.IntentExtrasKeys.NOTE_ID))
            noteId = getArguments().getLong(Constants.IntentExtrasKeys.NOTE_ID);

        subjectEditText = (EditText) view.findViewById(R.id.subjectEditText);
        detailEditText = (EditText) view.findViewById(R.id.detailEditText);

        Note note = getNoteWithId(getNoteId());

        if (note != null) {
            subjectEditText.setText(note.getSubject());
            detailEditText.setText(note.getDetail());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.notes_detail_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.note_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save the note into the local database
     */
    public void saveNote() {
        ContentValues values = new ContentValues();

        values.put(ClioContentProvider.NotesTable.COLUMN_ID, getNoteId());
        values.put(ClioContentProvider.NotesTable.COLUMN_SUBJECT, subjectEditText.getText().toString());
        values.put(ClioContentProvider.NotesTable.COLUMN_DETAIL, detailEditText.getText().toString());

        // Update the REST state based on whether it is a new note or not
        if (isNewNote()) {
            values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.POSTING);
        } else {
            values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.PUTING);
        }

        Uri uri = ClioContentProvider.getNotesUri(getMatterId());
        getContentResolver().insert(uri, values);

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    /**
     * The Matter ID for the note we are on
     * @return
     */
    public Long getMatterId() {
        return matterId;
    }

    /**
     * The Note ID.
     * This will be -1 when it is a new note otherwise
     * the ID will match the ID on clio's servers
     * @return
     */
    public Long getNoteId() {
        return noteId;
    }


    /**
     * Checks if the note we are view is a new note or editing a current one
     * @return true if it is a new {@link com.katbutler.clionotes.models.Note}
     */
    public boolean isNewNote() {
        return (getNoteId() == -1);
    }

    /**
     * Get the note from {@link com.katbutler.clionotes.db.ClioContentProvider} with noteId
     * @param nId
     * @return
     */
    private Note getNoteWithId(Long nId) {

        if(nId != -1) {
            Note note = new Note();

            String[] projection = new String[] {ClioContentProvider.NotesTable.COLUMN_ID,
                                                ClioContentProvider.NotesTable.COLUMN_SUBJECT,
                                                ClioContentProvider.NotesTable.COLUMN_DETAIL};

            Uri uri = ClioContentProvider.getNoteUri(getMatterId(), getNoteId());

            Cursor cursor = getContentResolver().query(uri, projection, null, null, ClioContentProvider.NotesTable.COLUMN_ID + " ASC LIMIT 1");

            if (cursor.moveToFirst()) {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_ID));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_SUBJECT));
                String detail = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DETAIL));

                note.setId(id);
                note.setSubject(subject);
                note.setDetail(detail);
            }

            return note;
        }

        return null;
    }
}
