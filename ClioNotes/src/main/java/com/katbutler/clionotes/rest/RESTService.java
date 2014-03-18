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

    /**
     * Do service startup tasks. Do things that need to be done before the service is officially started
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }


    /**
     * Handle an incoming intent. The Intent should have a {@link com.katbutler.clionotes.rest.RESTConstants.RequestTypes}
     * added as an IntExtra so the {@link com.katbutler.clionotes.rest.RESTService} knows what
     * REST Request to make to Clio.
     *
     * @throws java.lang.RuntimeException when using invalid {@link com.katbutler.clionotes.rest.RESTConstants.RequestTypes}
     * @param intent Intent used to start a Clio REST request
     */
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
            case RESTConstants.RequestTypes.DELETE_NOTE:
                noteId = intent.getLongExtra(RESTConstants.IntentExtraKeys.NOTE_ID, -1);

                deleteNote(noteId);

                break;
            default:
                throw new RuntimeException("Invalid RequestType. Cannot make REST request.");
        }
    }

    /**
     * Run the REST POST request to create a new {@link Note} for the given {@link com.katbutler.clionotes.models.Matter}
     * @param matterId
     * @param noteId
     */
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

    /**
     * Run the PUT request to update a {@link Note} with changes.
     * @param noteId
     */
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

    /**
     * Run the DELETE request to delete a {@link Note} from the Clio server.
     * @param noteId
     */
    public void deleteNote(final Long noteId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                RESTClient.RESTResponse resp = RESTClient.url(String.format(RESTConstants.ClioAPI.DELETE_NOTE_URL, noteId))
                        .withHeader("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a")
                        .delete();

                if (resp == null)
                    return;

                if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.OK) {
                    new RESTProcessor().processDeletedNote(noteId);
                } else if (resp.getStatusCode() == RESTConstants.RESTStatusCodes.RECORD_NOT_FOUND) {
                    //TODO a toast to say Record Not Found to delete
                }
            }
        }).start();
    }

    /**
     * Run the REST request to get all the {@link com.katbutler.clionotes.models.Matter Matters} for
     * the User.
     */
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

    /**
     * Run the REST request for all {@link Note Notes} regarding a {@link Matter}
     * @param matterId The ID for Matter we are getting notes for
     */
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