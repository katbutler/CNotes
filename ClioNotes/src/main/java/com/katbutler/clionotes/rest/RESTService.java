package com.katbutler.clionotes.rest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.katbutler.clionotes.models.Matters;

/**
 * Background Service to run the network REST request to web server.
 */
public class RESTService extends IntentService {

    public RESTService() {
        super("RESTService");
    }

    @Override
    public void onCreate() {
        // TODO do service startup tasks. Do things that need to be done before the service is officially started
        super.onCreate();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // TODO Setup Service Command
//        onHandleIntent(intent);
//        return Service.START_NOT_STICKY;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO create a Binder implementation to allow communication to this Service from the RESTServiceHelper
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Handle the intent
        int requestType = intent.getIntExtra(RESTConstants.IntentExtraKeys.REQUEST_TYPE, -1);

        switch(requestType) {
            case RESTConstants.RequestTypes.GET_ALL_MATTERS:
                getAllMatters();
                break;
            case RESTConstants.RequestTypes.GET_MATTER_WITH_ID:
                break;
            case RESTConstants.RequestTypes.GET_ALL_NOTES_FOR_MATTER:
                break;


            case RESTConstants.RequestTypes.POST_NEW_NOTE:
                break;

            default:
                //TODO throw exception
                stopSelf();
                break;
        }
    }

    public void getAllMatters() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RESTClient.RESTResponse resp = RESTClient.url(RESTConstants.ClioAPI.MATTERS_URL)
                        .withHeader("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a")
                        .withHeader("Accept", "application/json")
                        .withHeader("Content-Type", "application/json")
                        .get();

                // Response is null when no network contection
                if (resp == null)
                    return;

                if(resp.getStatusCode() == 200) {
                    Log.i("RSP", resp.getBody());
                    Gson gson = new Gson();

                    Matters matters = gson.fromJson(resp.getBody(), Matters.class);
                    // TODO pass matters to processor to be processed into the SQLite DB
                    new RESTProcessor().processMatters(matters);
                }
                //TODO handle other HTTP status codes
            }
        }).start();

    }
}