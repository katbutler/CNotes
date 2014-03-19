package com.katbutler.clionotes.fragments;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.katbutler.clionotes.ClioNotesActivity;
import com.katbutler.clionotes.R;
import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.db.ClioDatabaseQueryHelper;
import com.katbutler.clionotes.fragments.common.BackPressedHandler;
import com.katbutler.clionotes.models.Note;
import com.katbutler.clionotes.rest.RESTConstants;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * NoteDetailFragment used to edit/create {@link com.katbutler.clionotes.models.Note} details
 */
public class NoteDetailFragment extends Fragment {

    private Long matterId = -1l;
    private Long noteId = ClioContentProvider.createNewIndex();

    private BackPressedHandler backPressedHandler;

    private EditText subjectEditText;
    private EditText detailEditText;
    private EditText dateEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backPressedHandler = new BackPressedHandler() {
            @Override
            public void onBackPressed() {
                hideKeyboard();
            }
        };


        ((ClioNotesActivity)getActivity()).addBackPressedHandler(backPressedHandler);
    }

    @Override
    public void onDestroy() {
        ((ClioNotesActivity)getActivity()).removeBackPressedHandler(backPressedHandler);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from intents
        if (getArguments().containsKey(Constants.IntentExtrasKeys.MATTER_ID)) {
            matterId = getArguments().getLong(Constants.IntentExtrasKeys.MATTER_ID);
            getActivity().setTitle("New Note");
        }

        if (getArguments().containsKey(Constants.IntentExtrasKeys.NOTE_ID)) {
            noteId = getArguments().getLong(Constants.IntentExtrasKeys.NOTE_ID);
            getActivity().setTitle("Edit Note");
        }

        subjectEditText = (EditText) view.findViewById(R.id.subjectEditText);
        detailEditText = (EditText) view.findViewById(R.id.detailEditText);
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);

        Note note = ClioDatabaseQueryHelper.getNoteWithId(getContentResolver(), getNoteId());

        if (note != null) {
            if (note.getSubject() != null)
                subjectEditText.setText(note.getSubject());

            if (note.getDetail() != null)
                detailEditText.setText(note.getDetail());

            if (note.getDate() != null)
                dateEditText.setText(note.getDate());
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

        if (!validateNote())
            return;

        ContentValues values = new ContentValues();
        Uri uri;

        values.put(ClioContentProvider.NotesTable.COLUMN_ID, getNoteId());
        values.put(ClioContentProvider.NotesTable.COLUMN_SUBJECT, subjectEditText.getText().toString());
        values.put(ClioContentProvider.NotesTable.COLUMN_DETAIL, detailEditText.getText().toString());
        values.put(ClioContentProvider.NotesTable.COLUMN_DATE, dateEditText.getText().toString());

        // Update the REST state based on whether it is a new note or not
        if (isNewNote()) {
            uri = ClioContentProvider.getNotesUri(getMatterId());
            values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.POSTING);
            getContentResolver().insert(uri, values);
        } else {
            uri = ClioContentProvider.getNoteRegardMatterUri(getMatterId(), getNoteId());
            values.put(ClioContentProvider.NotesTable.COLUMN_REST_STATE, RESTConstants.RESTStates.PUTING);
            getContentResolver().update(uri, values, ClioContentProvider.NotesTable.COLUMN_ID+ "=?", new String[] {getNoteId().toString()});
        }

        hideKeyboard();

        getActivity().getSupportFragmentManager().popBackStack();
    }


    /**
     * Validates the notes fields and produces Toasts if invalid
     */
    private boolean validateNote() {
        if (subjectEditText.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Must specify a subject.", Toast.LENGTH_LONG).show();
            return false;
        }

        String date = dateEditText.getText().toString();

        if (!date.equals("")) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException pe) {
                Toast.makeText(getActivity().getApplicationContext(), "Date must be in the format yyyy-mm-dd", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    /**
     * Hide the keyboard if it is open
     */
    private void hideKeyboard() {
        // hide keyboard after hitting save
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(detailEditText.getWindowToken(), 0);
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
        return (getNoteId() < 0);
    }

}
