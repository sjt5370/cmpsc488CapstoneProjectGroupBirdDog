package edu.psu.sjt5370.stockingproto;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseManager {
    interface OnDatabaseReadyListener {
        void onDatabaseReady(Connection db);
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "warehouse.db";
    private static final String URL = "jdbc:mysql://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433/warehouse.db";
    private static final int PORT = 1433;
    private static final String USERNAME = "masterUser";
    private static final String PASSWORD = "master1234";
    private static final String CLEAR_TABLES =
                    "drop table if exists order_history;\n" +
                    "drop table if exists customer_account;\n" +
                    "drop table if exists employee_account;\n" +
                    "drop table if exists master_account;\n" +
                    "drop table if exists inventory;\n" +
                    "drop table if exists product;\n";
    private static final String PROD_TABLE =
            "create table product (prod_id int primary key, prod_name varchar(100), prod_desc varchar(255), manu varchar(50), price float, unique( prod_name, prod_desc, manu));";
    private static final String INV_TABLE =
            "create table inventory (prod_id int primary key, inv_bulk int not null, inv_shelf int not null, foreign key (prod_id) references product, check (not inv_bulk < 0), check (not inv_shelf < 0));";
    private static final String MASTER_ACC_TABLE =
            "create table master_account (acc_id int primary key, acc_type bit not null, password varchar(30) not null);";
    private static final String EMPL_ACC_TABLE =
            "create table employee_account (acc_id int primary key, job varchar(30), productivity int not null, foreign key(acc_id) references master_account);";
    private static final String CUST_ACC_TABLE =
            "create table customer_account (acc_id int primary key, acc_address varchar(255) not null, email varchar(50) not null, foreign key(acc_id) references master_account);";
    private static final String ORDER_HIST_TABLE =
            "create table order_history (acc_id int primary key, order_num int not null, prod_id int not null, quantity int not null, foreign key(acc_id) references customer_account, unique (order_num, prod_id), foreign key(prod_id) references product, check(not quantity < 0));";

    public DatabaseManager() {}

    public void addBasics() {    //FIXME: TEMPORARY, ONLY CALL ONCE
        getDBConnection(new OnDatabaseReadyListener() {
            @Override
            public void onDatabaseReady(Connection db) {
                System.out.println("Initializing!");
                Statement st = null;
                try {
                    st = db.createStatement();
                    st.executeUpdate("insert into master_account values (12345, 1, 'password');");
                    st.executeUpdate("insert into employee_account values (12345, 'stocker', 0);");
                } catch (SQLException | NullPointerException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                } finally {
                    try {
                        st.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }

                Random r = new Random();
                for (int i = 0; i < 20; i++) {
                    Product p = new Product();
                    p.setID(i + 1);
                    p.setShelfStock(r.nextInt(999) + 1);
                    p.setBulkStock(r.nextInt(999) + 1);
                    p.setManufacturer("TestManu #" + (r.nextInt(10) + 1));
                    p.setProductName("TestProd #" + (i + 1));
                    p.setDescription("An example product.");
                    p.setPrice(r.nextDouble() * 100);
                    addProduct(p, db);
                }
                try {
                    db.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    public static void addProduct(Product product, Connection db) {
        StringBuilder command1 = new StringBuilder();
        command1.append("insert into product values (");
        command1.append(product.getID());
        command1.append(", \'");
        command1.append(product.getProductName());
        command1.append("\', \'");
        command1.append(product.getDescription());
        command1.append("\', \'");
        command1.append(product.getManufacturer());
        command1.append("\', ");
        command1.append(product.getPrice());
        command1.append(");");
        StringBuilder command2 = new StringBuilder();
        command2.append("insert into inventory values (");
        command2.append(product.getID());
        command2.append(", ");
        command2.append(product.getBulkStock());
        command2.append(", ");
        command2.append(product.getShelfStock());
        command2.append(");");
        Statement st = null;
        try {
            st = db.createStatement();
            st.executeUpdate(command1.toString());
            st.executeUpdate(command2.toString());
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static Product getProduct(int id, Connection db) {
        StringBuilder command = new StringBuilder();
        command.append("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, inv_bulk, inv_shelf ");
        command.append("from product, inventory ");
        command.append("where product.prod_id = inventory.prod_id and product.prod_id = ");
        command.append(id);
        command.append(";");
        Product product = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = db.createStatement();
            rs = st.executeQuery(command.toString());
            if (rs.next()) {
                product = new Product();
                product.setID(rs.getInt("prod_id"));
                product.setProductName(rs.getString("prod_name"));
                product.setDescription(rs.getString("prod_desc"));
                product.setManufacturer(rs.getString("manu"));
                product.setPrice(rs.getDouble("price"));
                product.setBulkStock(rs.getInt("inv_bulk"));
                product.setShelfStock(rs.getInt("inv_shelf"));
            }
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                rs.close();
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        return product;
    }

    public static ArrayList<Product> getProductList(Connection db) {   //FIXME: Currently only returns all products with a nonzero bulk quantity
        ArrayList<Product> productList = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = db.createStatement();
            rs = st.executeQuery("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, inv_bulk, inv_shelf from product, inventory where product.prod_id = inventory.prod_id and not inv_bulk = 0;");
            while (rs.next()) {
                Product product = new Product();
                product.setID(rs.getInt("prod_id"));
                product.setProductName(rs.getString("prod_name"));
                product.setDescription(rs.getString("prod_desc"));
                product.setManufacturer(rs.getString("manu"));
                product.setPrice(rs.getDouble("price"));
                product.setBulkStock(rs.getInt("inv_bulk"));
                product.setShelfStock(rs.getInt("inv_shelf"));
                productList.add(product);
            }

        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                rs.close();
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        return productList;
    }

    public static void stockProduct(int id, int quantity, Connection db) {
        StringBuilder command = new StringBuilder();
        command.append("update inventory set inv_bulk = inv_bulk - ");
        command.append(quantity);
        command.append(", inv_shelf = inv_shelf + ");
        command.append(quantity);
        command.append(" where prod_id = ");
        command.append(id);
        command.append(";");
        Statement st = null;
        try {
            st = db.createStatement();
            st.executeUpdate(command.toString());
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void receiveProduct(int id, int quantity, Connection db) {
        StringBuilder command = new StringBuilder();
        command.append("update inventory set inv_bulk = inv_bulk + ");
        command.append(quantity);
        command.append(" where prod_id = ");
        command.append(id);
        command.append(";");
        Statement st = null;
        try {
            st = db.createStatement();
            st.executeUpdate(command.toString());
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static boolean authenticate(int id, String password, Connection db) {
        StringBuilder command = new StringBuilder();
        command.append("select acc_type, password, job ");
        command.append("from master_account, employee_account ");
        command.append("where master_account.acc_id = employee_account.acc_id and master_account.acc_id = ");
        command.append(id);
        command.append(";");
        Statement st = null;
        ResultSet rs = null;
        boolean acc_type = false;
        String correctPass = "", job = "";
        try {
            st = db.createStatement();
            rs = st.executeQuery(command.toString());
            if (!rs.next()) {
                rs.close();
                return false;
            }
            acc_type = (rs.getInt("acc_type") == 1);   //FIXME: true for employee or customer?
            job = rs.getString("job");
            correctPass = rs.getString("password");     //FIXME: needs encryption
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                rs.close();
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        return acc_type && job.toLowerCase().equals("stocker") && password.equals(correctPass);
    }

    public void getDBConnection(OnDatabaseReadyListener listener) { new OpenDatabaseAsyncTask().execute(listener); }

    public static class OpenDatabaseAsyncTask extends AsyncTask<OnDatabaseReadyListener, Void, Connection> {
        OnDatabaseReadyListener listener;

        @Override
        protected Connection doInBackground(OnDatabaseReadyListener... params) {
            listener = params[0];
            try {
                System.out.println("Connecting...");
                //Class.forName("com.mysql.jdbc.Driver");
                //DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
                Connection c = DriverManager.getConnection(URL, USERNAME, PASSWORD);    //FIXME: Hangs, permissions issue?
                System.out.println("Connected!");
                return c;
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Connection db) {
            listener.onDatabaseReady(db);
        }
    }
}