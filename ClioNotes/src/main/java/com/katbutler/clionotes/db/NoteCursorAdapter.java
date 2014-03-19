package com.katbutler.clionotes.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.katbutler.clionotes.R;


public class NoteCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public NoteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.note_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_ID));
        String subject = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_SUBJECT));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DETAIL));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(ClioContentProvider.NotesTable.COLUMN_DATE));

        // Set the ID as a tag to be used when the row is clicked
        view.setTag(id);


        if (subject != null) {
            TextView subjectText = (TextView) view.findViewById(R.id.subject_text);
            subjectText.setText(subject);
        }

        if (details != null) {
            TextView detailText = (TextView) view.findViewById(R.id.detail_text);
            detailText.setText(details);
        } else {
            TextView detailText = (TextView) view.findViewById(R.id.detail_text);
            detailText.setText("");
        }

        if (date != null) {
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            dateText.setText(date);
        } else {
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            dateText.setText("");
        }

    }
}
