package com.katbutler.clionotes.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.katbutler.clionotes.R;
import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.db.NoteCursorAdapter;

/**
 * NotesListFragment is the listview to view list of {@link com.katbutler.clionotes.models.Note Notes}
 */
public class NotesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteCursorAdapter adapter;
    private Long matterId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        matterId = getArguments().getLong(ClioContentProvider.MattersTable.COLUMN_ID);

        fillData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.notes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_note:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        return inflater.inflate(R.layout.notes_list_fragment, container, false);
    }


    /**
     * Fill the notes list from the Cursor
     */
    private void fillData() {

        getLoaderManager().initLoader(0, null, this);
//        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        adapter = new NoteCursorAdapter(this.getActivity(), null);

        setListAdapter(adapter);
    }

    /**
     * Sets up cursor loader to the {@link com.katbutler.clionotes.models.Note Notes}
     * in the ContentProvider
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = { ClioContentProvider.NotesTable.COLUMN_ID, ClioContentProvider.NotesTable.COLUMN_SUBJECT, ClioContentProvider.NotesTable.COLUMN_DETAIL};
        Uri notesUri = ClioContentProvider.getNotesUri(getMatterId());
        System.out.println(notesUri.toString());
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(), notesUri, projection, null, null, null);
        return cursorLoader;
    }

    /**
     * Replace the current cursor displayed by the cursor adapter
     * with the new result set
     * @param cursorLoader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    /**
     * Reset loader when terminating
     * @param cursorLoader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public void addNote() {

        if (getMatterId() != null) {
            NoteDetailFragment noteDetailFragment = new NoteDetailFragment();

            Bundle args = new Bundle();
            args.putLong(ClioContentProvider.MattersTable.COLUMN_ID, getMatterId());
            noteDetailFragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, noteDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private Long getMatterId() {
        return matterId;
    }
}
