package com.katbutler.clionotes.rest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.katbutler.clionotes.db.ClioDatabaseQueryHelper;
import com.katbutler.clionotes.models.ClioNote;
import com.katbutler.clionotes.models.Matters;
import com.katbutler.clionotes.models.Note;
import com.katbutler.clionotes.models.Notes;

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
        Long matterId;
        Long noteId;

        switch(requestType) {
            case RESTConstants.RequestTypes.GET_ALL_MATTERS:
                getAllMatters();
                break;
            case RESTConstants.RequestTypes.GET_MATTER_WITH_ID:
                break;
            case RESTConstants.RequestTypes.GET_ALL_NOTES_FOR_MATTER:
                matterId = intent.getLongExtra(RESTConstants.IntentExtraKeys.MATTER_ID, -1);

                if(matterId != -1) {
                    getAllNotesForMatter(matterId);
                }
                break;
            case RESTConstants.RequestTypes.GET_NOTE_WITH_ID:

                break;
            case RESTConstants.RequestTypes.POST_NEW_NOTE:
                matterId = intent.getLongExtra(RESTConstants.IntentExtraKeys.MATTER_ID, -1);
                noteId = intent.getLongExtra(RESTConstants.IntentExtraKeys.NOTE_ID, -1);

                if(matterId != -1) {
                    createNewNoteForMatter(matterId, noteId);
                }

                break;
            case RESTConstants.RequestTypes.PUT_NOTE:
                noteId = intent.getLongExtra(RESTConstants.IntentExtraKeys.NOTE_ID, -1);

                updateNote(noteId);

                break;

            default:
                //TODO throw exception
                stopSelf();
                break;
        }
    }

    private void createNewNoteForMatter(final Long matterId, final Long noteId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Note note = ClioDatabaseQueryHelper.getNoteWithId(getContentResolver(), noteId);
                note.setId(null); //clear negative ID

                ClioNote clioNote = new ClioNote(note);
                String body = new Gson().toJson(clioNote);

                RESTClient.RESTResponse resp = RESTClient.url(String.format(RESTConstants.ClioAPI.NEW_NOTE_URL, matterId))
                        .withHeader("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a")
                        .withHeader("Accept", "application/json")
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .post();


                // Response is null when no network contection
                if (resp == null)
                    return;

                if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.CREATED) {
                    Log.i("RSP", resp.getBody());
                    Gson gson = new Gson();

                    ClioNote noteRsp = gson.fromJson(resp.getBody(), ClioNote.class);
                    // TODO pass the note response and old noteId to the processor
                    new RESTProcessor().processCreatedNote(noteId, noteRsp);
                } else if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.BAD_REQUEST) {
                    Log.i("BAD RESPONSE", resp.getBody());
                }
                //TODO handle other HTTP status codes
            }
        }).start();
    }

    public void updateNote(final Long noteId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Note note = ClioDatabaseQueryHelper.getNoteWithId(getContentResolver(), noteId);

                ClioNote clioNote = new ClioNote(note);

                String body = new Gson().toJson(clioNote);

                RESTClient.RESTResponse resp = RESTClient.url(String.format(RESTConstants.ClioAPI.UPDATE_NOTE_URL, noteId))
                        .withHeader("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a")
                        .withHeader("Accept", "application/json")
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .put();

                // Response is null when no network contection
                if (resp == null)
                    return;

                if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.OK) {
                    Log.i("RSP", resp.getBody());
                    Gson gson = new Gson();

                    ClioNote noteRsp = gson.fromJson(resp.getBody(), ClioNote.class);
                    // TODO pass the note response and old noteId to the processor
                    new RESTProcessor().processUpdatedNote(noteId, noteRsp);
                } else if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.BAD_REQUEST) {
                    Log.i("BAD RESPONSE", resp.getBody());
                    //TODO ACTUALLY handle response. toast?
                } else if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.RECORD_NOT_FOUND) {
                    Log.i("RECORD NOT FOUND", resp.getBody());
                }
            }
        }).start();
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

                if(resp.getStatusCode() == RESTConstants.RESTStatusCodes.OK) {
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

    public void getAllNotesForMatter(final Long matterId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RESTClient.RESTResponse resp = RESTClient.url(String.format(RESTConstants.ClioAPI.MATTER_NOTE_URL, matterId))
                        .withHeader("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a")
                        .withHeader("Accept", "application/json")
                        .withHeader("Content-Type", "application/json")
                        .get();

                // Response is null when no network contection
                if (resp == null)
                    return;

                if(resp.getStatusCode() == RESTConstants.RESTStatusCodes.OK) {
                    Log.i("RSP", resp.getBody());
                    Gson gson = new Gson();

                    Notes notes = gson.fromJson(resp.getBody(), Notes.class);
                    // TODO pass matters to processor to be processed into the SQLite DB
                    new RESTProcessor().processNotesForMatter(notes, matterId);
                }
                //TODO handle other HTTP status codes
            }
        }).start();
    }
}