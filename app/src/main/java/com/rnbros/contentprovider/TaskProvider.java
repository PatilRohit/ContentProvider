package com.rnbros.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by rohitp on 3/2/2016.
 */
public class TaskProvider extends ContentProvider {

    private SQLiteDatabase db;
    private TaskDBHelper taskDBHelper;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.TABLE,TaskContract.TASKS_LIST);
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.TABLE+"/#",TaskContract.TASKS_ITEM);
    }

    @Override
    public boolean onCreate() {
        boolean ret = true;
        taskDBHelper = new TaskDBHelper(getContext());
        db = taskDBHelper.getWritableDatabase();

        if (db == null) {
            ret = false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            ret = false;
        }

        return ret;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TaskContract.TABLE);
        switch(uriMatcher.match(uri)){
            case TaskContract.TASKS_LIST:
                break;
            case TaskContract.TASKS_ITEM:
                qb.appendWhere(TaskContract.Columns._ID+" = "+uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid uri "+uri);
        }
        return qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TaskContract.TASKS_LIST:
                return TaskContract.CONTENT_TYPE;

            case TaskContract.TASKS_ITEM:
                return TaskContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(uriMatcher.match(uri) != TaskContract.TASKS_LIST){
            throw new IllegalArgumentException("Invalid URI: "+uri);
        }

        long id = db.insert(TaskContract.TABLE,null,values);
        if(id>0){
            return ContentUris.withAppendedId(uri,id);
        }
        throw new SQLException("Error inserting into table: "+TaskContract.TABLE);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted = 0;
        switch (uriMatcher.match(uri)){
            case TaskContract.TASKS_LIST:
                deleted = db.delete(TaskContract.TABLE,selection,selectionArgs);
                break;
            case TaskContract.TASKS_ITEM:
                String where = TaskContract.Columns._ID+" = "+uri.getLastPathSegment();
                if(!selection.isEmpty()){
                    where += " AND "+selection;
                }
                deleted = db.delete(TaskContract.TABLE,where,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated = 0;
        switch (uriMatcher.match(uri)){
            case TaskContract.TASKS_LIST:
                updated = db.update(TaskContract.TABLE, values, selection, selectionArgs);
                break;
            case TaskContract.TASKS_ITEM:
                String where = TaskContract.Columns._ID+" = "+uri.getLastPathSegment();
                if(!selection.isEmpty()){
                    where += " AND "+selection;
                }
                updated = db.update(TaskContract.TABLE,values,where,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }
        return updated;
    }
}
