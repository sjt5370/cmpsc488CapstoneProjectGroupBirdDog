package edu.psu.sjt5370.stockingproto;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {          //FIXME: USE WEAK REFERENCES FOR LISTENERS IN ASYNCTASKS
    interface OnAuthenticationListener { void onAuthentication(boolean authGranted); }
    interface OnGetProductListListener { void onGetProductList(ArrayList<Product> productList); }
    interface OnGetProductListener { void onGetProduct(Product product); }
    interface OnPasswordChangedListener { void OnPasswordChanged(boolean passChanged); }

    //public static final int DATABASE_VERSION = 1;
    //public static final String DATABASE_NAME = "warehouse.db";
    private static final String URL = "jdbc:jtds:sqlserver://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433;databaseName=warehouse;user=masterUser;password=master1234;sslProtocol=TLSv1";
    //private static final int PORT = 1433;
    //private static final String USERNAME = "masterUser";
    //private static final String PASSWORD = "master1234";

    public static void addProduct(Product product) { new AddProductAsyncTask().execute(product); }
    private static class AddProductAsyncTask extends AsyncTask<Product, Void, Void> {
        Product product;

        @Override
        protected Void doInBackground(Product... params) {
            product = params[0];
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
            command1.append(", ");
            command1.append(product.getPriority());
            command1.append(", ");
            command1.append(product.getVolume());
            command1.append(");");
            StringBuilder command2 = new StringBuilder();
            command2.append("insert into inventory values (");
            command2.append(product.getID());
            command2.append(", ");
            command2.append(product.getBulkStock());
            command2.append(", ");
            command2.append(product.getShelfStock());
            command2.append(", ");
            command2.append(product.stockRequested() ? 1 : 0);
            command2.append(");");
            Statement st = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                st.executeUpdate(command1.toString());
                st.executeUpdate(command2.toString());
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return null;
        }
    }

    public static void getProduct(int id, OnGetProductListener listener) { new GetProductAsyncTask().execute(id, listener);}
    private static class GetProductAsyncTask extends AsyncTask<Object, Void, Product> {
        int id;
        OnGetProductListener listener;

        @Override
        protected Product doInBackground(Object... params) {
            id = (int) params[0];
            listener = (OnGetProductListener) params[1];
            StringBuilder command = new StringBuilder();
            command.append("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, proirity, volume, inv_bulk, inv_shelf, stock_flag ");
            command.append("from product, inventory ");
            command.append("where product.prod_id = inventory.prod_id and product.prod_id = ");
            command.append(id);
            command.append(";");
            Product product = null;
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                if (rs.next()) {
                    product = new Product();
                    product.setID(rs.getInt("prod_id"));
                    product.setProductName(rs.getString("prod_name"));
                    product.setDescription(rs.getString("prod_desc"));
                    product.setManufacturer(rs.getString("manu"));
                    product.setPrice(rs.getDouble("price"));
                    product.setPriority(rs.getInt("proirity"));
                    product.setVolume(rs.getDouble("volume"));
                    product.setBulkStock(rs.getInt("inv_bulk"));
                    product.setShelfStock(rs.getInt("inv_shelf"));
                    if (rs.getInt("stock_flag") == 1) product.requestStock();
                }
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    rs.close();
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return product;
        }

        @Override
        protected void onPostExecute(Product product) { listener.onGetProduct(product); }
    }

    public static void getProductList(OnGetProductListListener listener) { new GetProductListAsyncTask().execute(listener); }
    private static class GetProductListAsyncTask extends AsyncTask<OnGetProductListListener, Void, ArrayList<Product>> {
        OnGetProductListListener listener;

        @Override
        protected ArrayList<Product> doInBackground(OnGetProductListListener... params) {
            listener = params[0];
            ArrayList<Product> productList = new ArrayList<>();
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, proirity, volume, inv_bulk, inv_shelf, stock_flag from product, inventory where product.prod_id = inventory.prod_id and (inv_shelf = 0 or stock_flag = 1) and not inv_bulk = 0;");
                while (rs.next()) {
                    Product product = new Product();
                    product.setID(rs.getInt("prod_id"));
                    product.setProductName(rs.getString("prod_name"));
                    product.setDescription(rs.getString("prod_desc"));
                    product.setManufacturer(rs.getString("manu"));
                    product.setPrice(rs.getDouble("price"));
                    product.setPriority(rs.getInt("proirity"));
                    product.setVolume(rs.getDouble("volume"));
                    product.setBulkStock(rs.getInt("inv_bulk"));
                    product.setShelfStock(rs.getInt("inv_shelf"));
                    if (rs.getInt("stock_flag") == 1) product.requestStock();
                    productList.add(product);
                }

            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    rs.close();
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return productList;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productList) { listener.onGetProductList(productList); }
    }

    public static void stockProduct(int id, int quantity) { new StockProductAsyncTask().execute(id, quantity); }
    private static class StockProductAsyncTask extends AsyncTask<Integer, Void, Void> {
        int id;
        int quantity;

        @Override
        protected Void doInBackground(Integer... params) {
            id = params[0];
            quantity = params[1];
            StringBuilder command = new StringBuilder();
            command.append("update inventory set inv_bulk = inv_bulk - ");
            command.append(quantity);
            command.append(", inv_shelf = inv_shelf + ");
            command.append(quantity);
            command.append(", stock_flag = 0 where prod_id = ");
            command.append(id);
            command.append(";");
            Statement st = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                st.executeUpdate(command.toString());
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return null;
        }
    }

    public static void receiveProduct(int id, int quantity) { new ReceiveProductAsyncTask().execute(id, quantity); }
    private static class ReceiveProductAsyncTask extends AsyncTask<Integer, Void, Void> {
        int id;
        int quantity;

        @Override
        protected Void doInBackground(Integer... params) {
            id = params[0];
            quantity = params[1];
            StringBuilder command = new StringBuilder();
            command.append("update inventory set inv_bulk = inv_bulk + ");
            command.append(quantity);
            command.append(" where prod_id = ");
            command.append(id);
            command.append(";");
            Statement st = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                st.executeUpdate(command.toString());
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return null;
        }
    }

    public static void changePassword(String username, String fname, String lname, int id, String newpass, OnPasswordChangedListener listener) {
        new ChangePasswordAsyncTask().execute(username, fname, lname, id, newpass, listener);
    }
    private static class ChangePasswordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        String username;
        String fname;
        String lname;
        int id;
        String newpass;
        OnPasswordChangedListener listener;

        @Override
        protected Boolean doInBackground(Object... params) {
            username = (String) params[0];
            fname = (String) params[1];
            lname = (String) params[2];
            id = (int) params[3];
            newpass = (String) params[4];
            listener = (OnPasswordChangedListener) params[5];
            StringBuilder command1 = new StringBuilder();
            command1.append("select username, first_name, last_name ");
            command1.append("from master_account, employee_account ");
            command1.append("where master_account.acc_id = employee_account.acc_id and master_account.acc_id = \'");
            command1.append(id);
            command1.append("\';");
            StringBuilder command2 = new StringBuilder();
            command2.append("update master_account set password = \'");
            command2.append(newpass);
            command2.append("\' where acc_id = ");
            command2.append(id);
            command2.append(";");

            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery(command1.toString());
                if (!rs.next() || !username.equals(rs.getString("username")) ||
                        !fname.equals(rs.getString("first_name")) || !lname.equals(rs.getString("last_name"))) return false;
                //rs.close();
                st.executeUpdate(command2.toString());
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    rs.close();
                    st.close();
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean passChanged) { listener.OnPasswordChanged(passChanged); }
    }

    public static void authenticate(String username, String password, OnAuthenticationListener listener) { new AuthenticateAsyncTask().execute(username, password, listener); }
    private static class AuthenticateAsyncTask extends AsyncTask<Object, Void, Boolean> {
        String username;
        String password;
        OnAuthenticationListener listener;

        @Override
        protected Boolean doInBackground(Object... params) {    //FIXME: SCRUB USERNAME OF SPECIAL CHARACTERS BEFORE CHECKING
            username = (String) params[0];
            password = (String) params[1];
            listener = (OnAuthenticationListener) params[2];
            StringBuilder command = new StringBuilder();
            command.append("select acc_type, password, job ");
            command.append("from master_account, employee_account ");
            command.append("where master_account.acc_id = employee_account.acc_id and master_account.username = \'");
            command.append(username);
            command.append("\';");
            Statement st = null;
            ResultSet rs = null;
            boolean acc_type = false;
            String correctPass = "", job = "";

            Connection c = connect();
            try {
                st = c.createStatement();
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
                    disconnect(c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return acc_type && job.toLowerCase().equals("stocker") && password.equals(correctPass);
        }
        @Override
        protected void onPostExecute(Boolean authGranted) { listener.onAuthentication(authGranted); }
    }

    private static Connection connect() {
        try {
            System.out.println("Connecting...");
            Connection c = DriverManager.getConnection(URL);
            System.out.println("Connected!");
            return c;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    private static void disconnect(Connection connection) {
        System.out.println("Disconnecting...");
        try {
            connection.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            System.exit(1);
        }
        System.out.println("Disconnected!");
    }
}