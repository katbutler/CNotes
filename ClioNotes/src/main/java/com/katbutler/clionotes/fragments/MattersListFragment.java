package com.katbutler.clionotes.fragments;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.katbutler.clionotes.R;
import com.katbutler.clionotes.db.ClioContentProvider;

/**
 * MattersListFragment is the listview to view list of {@link com.katbutler.clionotes.models.Matter Matters}
 */
public class MattersListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;

    public MattersListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fillData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.matters_list_fragment, container, false);
    }


    /**
     * Called when clicking a {@link com.katbutler.clionotes.models.Matter} from the list
     * @param listView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // TODO load the NotesListFragment for the selected matter
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, new NotesListFragment())
                .addToBackStack(null)
                .commit();
        super.onListItemClick(listView, view, position, id);
    }

    /**
     * Fill the matters list from the Cursor
     */
    private void fillData() {

        String[] from = new String[] { ClioContentProvider.MattersTable.COLUMN_DISPLAY_NUMBER };
        int[] to = new int[] { R.id.label };

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.matter_row, null, from, to, 0);

        setListAdapter(adapter);
    }


    /**
     * Sets up cursor loader to the {@link com.katbutler.clionotes.models.Matter Matters}
     * in the ContentProvider
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = { ClioContentProvider.MattersTable.COLUMN_ID, ClioContentProvider.MattersTable.COLUMN_DISPLAY_NUMBER};
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(), ClioContentProvider.CONTENT_URI, projection, null, null, null);
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
}
