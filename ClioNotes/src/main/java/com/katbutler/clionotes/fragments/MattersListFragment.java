package com.katbutler.clionotes.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleCursorAdapter;

/**
 * MattersListFragment is the listview to view list of {@link com.katbutler.clionotes.models.Matter Matters}
 */
public class MattersListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;

    /**
     * Sets up cursor loader to the {@link com.katbutler.clionotes.models.Matter Matters}
     * in the ContentProvider
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        //TODO create cursor loader for Matters
        return null;
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
