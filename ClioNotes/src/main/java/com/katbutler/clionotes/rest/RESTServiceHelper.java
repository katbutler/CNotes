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

    /**
     * Set the context to run the commands on
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Check the Android system to see if the Network is available
     * @return
     */
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
        if (!isNetworkAvailable())
            return;

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
        if (!isNetworkAvailable())
            return;

        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.GET_ALL_NOTES_FOR_MATTER);
        i.putExtra(RESTConstants.IntentExtraKeys.MATTER_ID, id);

        context.startService(i);
    }

    /**
     * Start the RESTService in the background to create a new note
     * @param matterId The ID of the matter to create a new note for
     * @param noteId The temp negative ID of the note in the local database
     */
    public void createNote(Long matterId, Long noteId) {
        if (!isNetworkAvailable())
            return;

        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.POST_NEW_NOTE);
        i.putExtra(RESTConstants.IntentExtraKeys.MATTER_ID, matterId);
        i.putExtra(RESTConstants.IntentExtraKeys.NOTE_ID, noteId);

        context.startService(i);
    }

    /**
     * Start the RESTService in the background to update a note
     * @param noteId The ID of the note to update
     */
    public void updateNote(Long noteId) {
        if (!isNetworkAvailable())
            return;

        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.PUT_NOTE);
        i.putExtra(RESTConstants.IntentExtraKeys.NOTE_ID, noteId);

        context.startService(i);
    }

    /**
     * Start the RESTService in the background to delete a note
     * @param noteId The ID of the note to delete from Clio
     */
    public void deleteNote(Long noteId) {
        if (!isNetworkAvailable())
            return;

        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.DELETE_NOTE);
        i.putExtra(RESTConstants.IntentExtraKeys.NOTE_ID, noteId);

        context.startService(i);

    }

}
