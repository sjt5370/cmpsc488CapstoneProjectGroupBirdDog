package edu.psu.sjt5370.stockingproto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {
    interface OnDatabaseReadyListener {
        void onDatabaseReady(SQLiteDatabase db);
    }

    private static DatabaseManager instance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "";
    private static ArrayList<String> addProductQueue;

    private DatabaseManager(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        addProductQueue = new ArrayList<>();
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) instance = new DatabaseManager(context);
        return instance;
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
            return DatabaseManager.instance.getWritableDatabase();
        }
        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            listener.onDatabaseReady(db);
        }
    }

    public void addProduct(Product product, SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();
        command.append("insert into product values (");
        command.append(product.getID());
        command.append(", \'");
        command.append(product.getProductName());
        command.append("\', \'");
        command.append(product.getDescription());
        command.append("\', \'");
        command.append(product.getManufacturer());
        command.append("\', ");
        command.append(product.getPrice());
        command.append(");");
        db.rawQuery(command.toString(), null);
    }

    public Product getProduct(int id, SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();
        command.append("select prod_name, prod_desc, manu, price, inv_bulk, inv_shelf ");
        command.append("from product, inventory ");
        command.append("where product.prod_id = inventory.prod_id and product.prod_id = ");
        command.append(id);
        command.append(";");
        Cursor c = db.rawQuery(command.toString(), null);
        Product product = new Product();
        product.setID(c.getInt(c.getColumnIndex("prod_id")));
        product.setProductName(c.getString(c.getColumnIndex("prod_name")));
        product.setDescription(c.getString(c.getColumnIndex("prod_desc")));
        product.setManufacturer(c.getString(c.getColumnIndex("manu")));
        product.setPrice(c.getDouble(c.getColumnIndex("price")));
        product.setBulkStock(c.getInt(c.getColumnIndex("inv_bulk")));
        product.setShelfStock(c.getInt(c.getColumnIndex("inv_shelf")));
        c.close();
        return product;
    }

    public boolean authenticate(int id, String password, SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();
        command.append("select acc_type, password, job ");
        command.append("from master_account, employee_account ");
        command.append("where master_account.acc_id = employee_account.acc_id and account.id = ");
        command.append(id);
        command.append(";");
        Cursor c = db.rawQuery(command.toString(), null);
        boolean acc_type = (c.getInt(c.getColumnIndex("acc_type")) == 1);   //FIXME: true for employee or customer?
        String correctPass = c.getString(c.getColumnIndex("password"));     //FIXME: needs encryption
        String job = c.getString(c.getColumnIndex("job"));
        c.close();
        return acc_type && job.toLowerCase().equals("stocker") && password.equals(correctPass);
    }
}