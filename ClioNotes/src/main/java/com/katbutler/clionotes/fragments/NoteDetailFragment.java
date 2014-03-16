package com.katbutler.clionotes.fragments;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
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

/**
 * NoteDetailFragment used to edit/create {@link com.katbutler.clionotes.models.Note} details
 */
public class NoteDetailFragment extends Fragment {

    private Long matterId;


    private EditText subjectEditText;
    private EditText detailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO initialize fragment
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        matterId = getArguments().getLong(ClioContentProvider.MattersTable.COLUMN_ID);

        System.out.println(matterId);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subjectEditText = (EditText) view.findViewById(R.id.subjectEditText);
        detailEditText = (EditText) view.findViewById(R.id.detailEditText);
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
        values.put(ClioContentProvider.NotesTable.COLUMN_ID, -1l);
        values.put(ClioContentProvider.NotesTable.COLUMN_SUBJECT, subjectEditText.getText().toString());
        values.put(ClioContentProvider.NotesTable.COLUMN_DETAIL, detailEditText.getText().toString());

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
}
