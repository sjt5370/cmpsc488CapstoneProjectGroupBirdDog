package edu.psu.sjt5370.stockingproto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileReader;

public class DatabaseManager extends SQLiteOpenHelper {
    interface OnDatabaseReadyListener {
        void onDatabaseReady(SQLiteDatabase db);
    }

    private static DatabaseManager manager;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "";

    private DatabaseManager(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (manager == null) manager = new DatabaseManager(context);
        return manager;
    }

    private static final String CLEAR_TABLES =
                    "drop table if exists order_history;\n" +
                    "drop table if exists customer_account;\n" +
                    "drop table if exists employee_account;\n" +
                    "drop table if exists master_account;\n" +
                    "drop table if exists inventory;\n" +
                    "drop table if exists product;\n";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new FileReader("../../../../../text/create_tables.txt"));
            while ((line = br.readLine()) != null) sb.append(line);
            command = sb.toString();
        } catch (Exception fnfex) {
            command = "";
        }
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
        db.execSQL(CLEAR_TABLES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVers, int newVers) {
        onUpgrade(db, oldVers, newVers);
    }

    public void getWritableDatabase(OnDatabaseReadyListener listener) {
       new OpenDatabaseAsyncTask().execute(listener);
    }

    public class OpenDatabaseAsyncTask extends AsyncTask<OnDatabaseReadyListener, Void, SQLiteDatabase> {
        OnDatabaseReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDatabaseReadyListener... params) {
            listener = params[0];
            return DatabaseManager.manager.getWritableDatabase();
        }
        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            listener.onDatabaseReady(db);
        }
    }

}