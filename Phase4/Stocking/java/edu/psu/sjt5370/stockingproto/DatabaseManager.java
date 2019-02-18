package edu.psu.sjt5370.stockingproto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {
    interface OnDatabaseReadyListener {
        void onDatabaseReady(SQLiteDatabase db);
    }

    private static DatabaseManager instance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stocking_database.db";

    private DatabaseManager(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
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

    private static final String PROD_TABLE =
            "create table product(prod_id int primary key, prod_name varchar(100), prod_desc varchar(255), manu varchar(50), price float, unique( prod_name, prod_desc, manu));";
    private static final String INV_TABLE =
            "create table inventory(prod_id int primary key, inv_bulk int not null, inv_shelf int not null, foreign key (prod_id) references product, check (not inv_bulk < 0), check (not inv_shelf < 0));";
    private static final String MASTER_ACC_TABLE =
            "create table master_account (acc_id int primary key, acc_type bit not null, password varchar(30) not null);";
    private static final String EMPL_ACC_TABLE =
            "create table employee_account (acc_id int primary key, job varchar(30), productivity int not null, foreign key(acc_id) references master_account);";
    private static final String CUST_ACC_TABLE =
            "create table customer_account (acc_id int primary key, acc_address varchar(255) not null, email varchar(50) not null, foreign key(acc_id) references master_account);";
    private static final String ORDER_HIST_TABLE =
            "create table order_history (acc_id int primary key, order_num int not null, prod_id int not null, quantity int not null, foreign key(acc_id) references customer_account, unique (order_num, prod_id), foreign key(prod_id) references product, check(not quantity < 0));";

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String command;
        try {
            String location = getClass().getClassLoader().getResource(".").getPath() + "app/src/main/text/create_tables.txt";
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new FileReader(location));
            while ((line = br.readLine()) != null) sb.append(line);
            command = sb.toString();
            System.out.println(command);
            db.execSQL(command);
            createTempAccount();
        } catch (Exception fnfex) {
            Log.e("DatabaseManager", "Table failed to initialize.");
            System.exit(1);
        }
        */
        db.execSQL(PROD_TABLE);
        db.execSQL(INV_TABLE);
        db.execSQL(MASTER_ACC_TABLE);
        db.execSQL(EMPL_ACC_TABLE);
        db.execSQL(CUST_ACC_TABLE);
        db.execSQL(ORDER_HIST_TABLE);

        db.execSQL("insert into master_account values (12345, 1, 'password');");
        db.execSQL("insert into employee_account values (12345, 'stocker', 0);");
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

        command = new StringBuilder();
        command.append("insert into inventory values (");
        command.append(product.getID());
        command.append(", ");
        command.append(product.getBulkStock());
        command.append(", ");
        command.append(product.getShelfStock());
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
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
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

    public ArrayList<Product> getProductList(SQLiteDatabase db) {
        Cursor c = db.rawQuery("select prod_name, prod_desc, manu, price, inv_bulk, inv_shelf from product, inventory where product.prod_id = inventory.prod_id;", null);
        ArrayList<Product> productList = new ArrayList<>();
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        for (; !c.isAfterLast(); c.moveToNext()) {
            Product product = new Product();
            product.setID(c.getInt(c.getColumnIndex("prod_id")));
            product.setProductName(c.getString(c.getColumnIndex("prod_name")));
            product.setDescription(c.getString(c.getColumnIndex("prod_desc")));
            product.setManufacturer(c.getString(c.getColumnIndex("manu")));
            product.setPrice(c.getDouble(c.getColumnIndex("price")));
            product.setBulkStock(c.getInt(c.getColumnIndex("inv_bulk")));
            product.setShelfStock(c.getInt(c.getColumnIndex("inv_shelf")));
            productList.add(product);
        }
        c.close();
        return productList;
    }

    public void stockProduct(int id, int quantity, SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();
        command.append("update inventory set inv_bulk = inv_bulk - ");
        command.append(quantity);
        command.append(", inv_shelf = inv_shelf + ");
        command.append(quantity);
        command.append(" where prod_id = ");
        command.append(id);
        command.append(";");
        db.rawQuery(command.toString(), null);
    }

    public boolean authenticate(int id, String password, SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();
        command.append("select acc_type, password, job ");
        command.append("from master_account, employee_account ");
        command.append("where master_account.acc_id = employee_account.acc_id and master_account.acc_id = ");
        command.append(id);
        command.append(";");
        Cursor c = db.rawQuery(command.toString(), null);
        if (!c.moveToFirst()) {
            c.close();
            return false;
        }
        boolean acc_type = (c.getInt(c.getColumnIndex("acc_type")) == 1);   //FIXME: true for employee or customer?
        String correctPass = c.getString(c.getColumnIndex("password"));     //FIXME: needs encryption
        String job = c.getString(c.getColumnIndex("job"));
        c.close();
        return acc_type && job.toLowerCase().equals("stocker") && password.equals(correctPass);
    }
}