package com.katbutler.clionotes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.katbutler.clionotes.fragments.MattersListFragment;
import com.katbutler.clionotes.fragments.common.BackPressedHandler;
import com.katbutler.clionotes.rest.RESTServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class ClioNotesActivity extends ActionBarActivity {

    // Handlers for the back button pressed event
    private List<BackPressedHandler> backPressedHandlers = new ArrayList<BackPressedHandler>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For now to give the Service Helper the context
        RESTServiceHelper.getInstance().setContext(getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MattersListFragment())
                    .commit();
        }

    }


    @Override
    public void onBackPressed() {
        for (BackPressedHandler handler : backPressedHandlers) {
            handler.onBackPressed();
        }
        super.onBackPressed();
    }

    /**
     * Add a BackPressedHandler to listen to back pressed events
     * @param handler
     */
    public void addBackPressedHandler(BackPressedHandler handler) {
        backPressedHandlers.add(handler);
    }

    /**
     * Removed the BackPressedHandler that was added.
     * @param handler
     */
    public void removeBackPressedHandler(BackPressedHandler handler) {
        backPressedHandlers.remove(handler);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();

        for (BackPressedHandler handler : backPressedHandlers) {
            handler.onBackPressed();
        }

        return super.onSupportNavigateUp();
    }
}
