package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public abstract class DataItem implements BaseColumns {

    public DataItem() {}

    public DataItem(final Cursor cursor) {}

    public abstract ContentValues getContentValues();

    public abstract String getTableName();

    public abstract long getId();

    public abstract void setId(final long id);

    public Uri getUri() {
        return Uri.withAppendedPath(getBaseUri(), Long.toString(getId()));
    }

    public Uri getBaseUri() {
        return Uri.withAppendedPath(
                Uri.parse(LibraryProvider.SCHEME
                        + LibraryProvider.AUTHORITY), getTableName());
    }

    public void notifyProvider(final Context context) {
        try {
            context.getContentResolver().notifyChange(getUri(), null, false);
        }
        catch (UnsupportedOperationException e) {
        }
    }
}
