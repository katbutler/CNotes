package com.katbutler.clionotes.rest;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.katbutler.clionotes.models.*;

/**
 * {@link RESTServiceHelper} Singleton helper class to start background services
 * that manage the network REST operations.
 */
public class RESTServiceHelper {
    private Context context;

    private static RESTServiceHelper instance;


    /**
     * Default constructor for {@link RESTServiceHelper}
     */
    public RESTServiceHelper() {

    }

    /**
     * @return The Signleton instance of the {@link RESTServiceHelper}
     */
    public static RESTServiceHelper getInstance() {
        if (instance == null)
            instance = new RESTServiceHelper();

        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Start a service to request all {@link Matter Matters}
     */
    public void fetchMatters() {
        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.GET_ALL_MATTERS);

        // TODO Bind to service to check when it is completed

        context.startService(i);
    }

    /**
     * Start a service to request a {@link Matter} with id
     * @param id the id of the {@link Matter} we are trying to fetch
     */
    public void fetchNotesForMatter(Long id) {
        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.GET_ALL_NOTES_FOR_MATTER);
        i.putExtra(RESTConstants.IntentExtraKeys.MATTER_ID, id);

        context.startService(i);
    }

    public void createNote(Long matterId, Long noteId) {
        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.GET_ALL_NOTES_FOR_MATTER);
        i.putExtra(RESTConstants.IntentExtraKeys.MATTER_ID, matterId);
        i.putExtra(RESTConstants.IntentExtraKeys.NOTE_ID, noteId);

        context.startService(i);
    }

}
