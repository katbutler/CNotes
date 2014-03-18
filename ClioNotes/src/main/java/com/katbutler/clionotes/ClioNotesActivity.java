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

    /**
     * onCreate Activity event. Setup main fragment and Service Helper context
     * @param savedInstanceState
     */
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


    /**
     * Handle when back button is pressed. Also notify
     * all observers of this event.
     */
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

    /**
     * Handle when the Navigate Up is pressed
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();

        for (BackPressedHandler handler : backPressedHandlers) {
            handler.onBackPressed();
        }

        return super.onSupportNavigateUp();
    }
}
