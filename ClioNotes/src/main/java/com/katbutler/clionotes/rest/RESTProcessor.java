package com.katbutler.clionotes.rest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.katbutler.clionotes.db.ClioContentProvider;
import com.katbutler.clionotes.models.Matters;

/**
 * {@link RESTProcessor} processes the REST responses recieved from the server.
 *
 */
public class RESTProcessor {

    private Context context;

    public RESTProcessor() {
        context = RESTServiceHelper.getInstance().getContext();
    }


    public void processMatters(Matters matters) {
//
//        for (Matter matter : matters.getMatters()) {
//            Uri matterUri = Uri.parse(ClioContentProvider.CONTENT_URI + "/" + matter.getId());
//
//            if (getContentResolver().update(ClioContentProvider.CONTENT_URI, values, null, null) == 0) {
//                getContentResolver().insert(ClioContentProvider.CONTENT_URI, values);
//            }
//        }

        getContentResolver().bulkInsert( ClioContentProvider.CONTENT_URI, matters.getContentValues().toArray( new ContentValues[0] ) );
    }

    /**
     * Helper method to get the Content Resolver from the context
     * @return
     */
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }
}
