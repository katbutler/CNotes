package com.katbutler.clionotes.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.katbutler.clionotes.R;


public class MatterCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public MatterCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.matter_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_ID));
        String displayNumber = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_DISPLAY_NUMBER));
//        String status = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_STATUS));
//        String description = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_DESCRIPTION));
//        String openDate = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_OPEN_DATE));
//        String closeDate = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_CLOSE_DATE));
//        String pendingDate = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_PENDING_DATE));
//        String restState = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_REST_STATE));
//        String restResponse = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.MattersTable.COLUMN_REST_RESPONSE));

        // Set the ID as a tag to be used when the row is clicked
        view.setTag(id);


        if (displayNumber != null) {
            TextView displayNumberText = (TextView) view.findViewById(R.id.display_number_text);
            displayNumberText.setText(displayNumber);
        }

    }
}
