package com.katbutler.clionotes.rest;

import android.content.Context;
import android.content.Intent;
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
    public void fetchMatter(String id) {
        // Create the intent for the RESTService
        Intent i = new Intent(context, RESTService.class);

        // Add required REST data to intent
        i.putExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, RESTConstants.RequestTypes.GET_MATTER_WITH_ID);

        context.startService(i);
    }

}
