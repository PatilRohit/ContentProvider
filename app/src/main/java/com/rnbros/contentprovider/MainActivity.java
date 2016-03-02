package com.rnbros.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentValues values = new ContentValues();

        values.clear();
        values.put(TaskContract.Columns.TASK, "Content Provide");

        Uri uri = TaskContract.CONTENT_URI;
        uri = getApplicationContext().getContentResolver().insert(uri, values);

        Log.e("Inserted Values", "" + uri);

        uri = TaskContract.CONTENT_URI;
        int deleted = getApplicationContext().getContentResolver().delete(uri, TaskContract.Columns._ID + "=?", new String[]{"6"});

        Log.e("Deleted Values",""+deleted);

        uri = TaskContract.CONTENT_URI;
        int updated = getApplicationContext().getContentResolver().update(uri, values, TaskContract.Columns._ID + "=?", new String[]{"6"});

        Log.e("Updated Values",""+updated);

        Cursor cr = getContentResolver().query(uri,new String[]{TaskContract.Columns.TASK,TaskContract.Columns._ID},null,null,null);
        if(cr != null)
            while(cr.moveToNext()){
                Log.e("Cursor Values",""+cr.getString(0));
                Log.e("Cursor Values",""+cr.getString(1));
            }
    }
}
