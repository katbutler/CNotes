package com.katbutler.clionotes.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.katbutler.clionotes.R;
import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.db.NoteCursorAdapter;
import com.katbutler.clionotes.rest.RESTServiceHelper;

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
        getActivity().setTitle("Notes");

        return inflater.inflate(R.layout.notes_list_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Long noteId = (Long) view.getTag();

        if (noteId != null) {

            NoteDetailFragment noteDetailFragment = new NoteDetailFragment();

            Bundle args = new Bundle();
            args.putLong(Constants.IntentExtrasKeys.MATTER_ID, getMatterId());
            args.putLong(Constants.IntentExtrasKeys.NOTE_ID, noteId);
            noteDetailFragment.setArguments(args);


            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, noteDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
        super.onListItemClick(listView, view, position, id);
    }



    /**
     * Fill the notes list from the Cursor
     */
    private void fillData() {

        getLoaderManager().initLoader(0, null, this);
//        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        adapter = new NoteCursorAdapter(this.getActivity(), null);

        setListAdapter(adapter);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Long noteId = (Long) v.getTag();
        //TODO pass in noteid from view tag

//        menu.add(0, noteId, 0, "Delete");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("Delete")) {
            //TODO delete note from local database and send delete request to clio
        }
        return super.onContextItemSelected(item);
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
            args.putLong(Constants.IntentExtrasKeys.MATTER_ID, getMatterId());
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
