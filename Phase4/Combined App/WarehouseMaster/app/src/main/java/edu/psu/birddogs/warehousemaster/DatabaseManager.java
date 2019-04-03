package edu.psu.birddogs.warehousemaster;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DatabaseManager {          //FIXME: USE WEAK REFERENCES FOR LISTENERS IN ASYNCTASKS
    interface OnAuthenticationListener { void onAuthentication(boolean authGranted, String job); }
    interface OnGetProductListListener { void onGetProductList(ArrayList<StockProduct> productList); }
    interface OnGetProductListener { void onGetProduct(StockProduct product); }
    interface OnPasswordChangedListener { void OnPasswordChanged(boolean passChanged); }
    interface OnGetPalletListener { void onGetPallet(HashMap<Integer, PickProduct> pallet);}
    interface OnGetUsersListener { void onGetUsers(User users);}

    private static final String URL = "jdbc:jtds:sqlserver://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433;databaseName=warehouse;user=masterUser;password=master1234;sslProtocol=TLSv1";
    public static int curr_id = 0;

    public static void addProduct(StockProduct product) { new AddProductAsyncTask().execute(product); }
    private static class AddProductAsyncTask extends AsyncTask<StockProduct, Void, Void> {
        StockProduct product;

        @Override
        protected Void doInBackground(StockProduct... params) {
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
    private static class GetProductAsyncTask extends AsyncTask<Object, Void, StockProduct> {
        int id;
        OnGetProductListener listener;

        @Override
        protected StockProduct doInBackground(Object... params) {
            id = (int) params[0];
            listener = (OnGetProductListener) params[1];
            StringBuilder command = new StringBuilder();
            command.append("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, proirity, volume, inv_bulk, inv_shelf, stock_flag ");
            command.append("from product, inventory ");
            command.append("where product.prod_id = inventory.prod_id and product.prod_id = ");
            command.append(id);
            command.append(";");
            StockProduct product = null;
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                if (rs.next()) {
                    product = new StockProduct();
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
        protected void onPostExecute(StockProduct product) { listener.onGetProduct(product); }
    }

    public static void getProductList(OnGetProductListListener listener) { new GetProductListAsyncTask().execute(listener); }
    private static class GetProductListAsyncTask extends AsyncTask<OnGetProductListListener, Void, ArrayList<StockProduct>> {
        OnGetProductListListener listener;

        @Override
        protected ArrayList<StockProduct> doInBackground(OnGetProductListListener... params) {
            listener = params[0];
            ArrayList<StockProduct> productList = new ArrayList<>();
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery("select product.prod_id as prod_id, prod_name, prod_desc, manu, price, proirity, volume, inv_bulk, inv_shelf, stock_flag from product, inventory where product.prod_id = inventory.prod_id and (inv_shelf = 0 or stock_flag = 1) and not inv_bulk = 0;");
                while (rs.next()) {
                    StockProduct product = new StockProduct();
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
        protected void onPostExecute(ArrayList<StockProduct> productList) { listener.onGetProductList(productList); }
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

    public static void getNewPallet(OnGetPalletListener listener) { new getNewPalletAsync().execute(listener);}
    private static class getNewPalletAsync extends AsyncTask<OnGetPalletListener, Void, HashMap<Integer, PickProduct>>{
        OnGetPalletListener listener;

        @Override
        protected HashMap<Integer, PickProduct> doInBackground(OnGetPalletListener... params){
            listener = params[0];
            StringBuilder command = new StringBuilder();
            command.append("select pallet_id, pallet.prod_id as prod_id, prod_name, prod_desc, manu, proirity, quantity " +
                    "from pallet, product " +
                    "where pallet.prod_id = product.prod_id and pallet.pallet_id = " +
                    "(select min(pallet_id) as next_pallet from pallet " +
                    "where fulfilled = 0 and hold = 0 and order_num = " +
                    "(select min(order_num) as next_order from order_full " +
                    "where complete = 0 and urgency <= all(select urgency from order_full)));");
            System.out.println(command.toString());

            HashMap<Integer, PickProduct> pallet = new HashMap<>();
            PickProduct product = null;
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try{
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                while(rs.next()){
                    product = new PickProduct();
                    curr_id = rs.getInt("pallet_id");
                    product.setID(rs.getInt("prod_id"));
                    product.setName(rs.getString("prod_name"));
                    product.setDescription(rs.getString("prod_desc"));
                    product.setManu(rs.getString("manu"));
                    product.setPriority(rs.getInt("proirity"));
                    product.setQuantity(rs.getInt("quantity"));
                    pallet.put(product.getID(), product);
                }
            } catch (SQLException | NullPointerException ex){
                ex.printStackTrace();
                System.exit(1);
            }finally{
                try{
                    rs.close();
                    st.close();
                    disconnect(c);
                } catch(Exception ex){
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return pallet;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, PickProduct> pallet){ listener.onGetPallet(pallet);}
    }

    public static void removeInventory(int id, int quant) { new removeInventoryAsyncTask().execute(id, quant); }
    private static class removeInventoryAsyncTask extends AsyncTask<Integer, Void, Void> {
        int id;
        int quant;
        @Override
        protected Void doInBackground(Integer... params){
            id = params[0];
            quant = params[1];

            StringBuilder command = new StringBuilder();
            String cmd = "update inventory set inv_shelf = inv_shelf - "+quant+" where prod_id = " + id + ";";
            command.append(cmd);

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

    public static void returnInventory(HashMap<Integer, PickProduct> products) { new returnInventoryAsyncTask().execute(products); }
    private static class returnInventoryAsyncTask extends AsyncTask<HashMap<Integer, PickProduct>, Void, Void> {
        HashMap<Integer, PickProduct> prods;
        @Override
        protected Void doInBackground(HashMap<Integer, PickProduct> ... params){
            prods = params[0];
            StringBuilder command = new StringBuilder();
            String cmd = "";

            Statement st = null;
            Connection c = connect();
            Set<Integer> keys = prods.keySet();
            for(Integer key : keys){
                cmd = "update inventory set inv_shelf = inv_shelf + " + prods.get(key).getQuantity() + " where prod_id = " + key + ";";
                command.append(cmd);
                try {
                    st = c.createStatement();
                    st.executeUpdate(command.toString());
                    command = new StringBuilder();
                } catch (SQLException | NullPointerException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            command = new StringBuilder();
            cmd = "update pallet set hold = 1 where pallet_id = " + curr_id + ";";
            command.append(cmd);
            try {
                st = c.createStatement();
                st.executeUpdate(command.toString());
            } catch (SQLException | NullPointerException ex){
                ex.printStackTrace();
                System.exit(1);
            }
            try {
                st.close();
                disconnect(c);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            return null;
        }
    }

    public static void getUsers(OnGetUsersListener listener, String un) { new getUsersAsync(un).execute(listener);}
    private static class getUsersAsync extends AsyncTask<OnGetUsersListener, Void, User>{
        OnGetUsersListener listener;
        String userName;

        public getUsersAsync(String un){
            userName = un;
        }

        @Override
        protected User doInBackground(OnGetUsersListener... params){
            listener = params[0];
            StringBuilder command = new StringBuilder();
            command.append("select master_account.acc_id, acc_type, username, password, first_name, last_name, job, productivity ");
            command.append("from master_account, employee_account ");
            command.append("where master_account.acc_id = employee_account.acc_id and username = '" + userName + "';");
            System.out.println(command.toString());

            User user = null;
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try{
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                if(rs.next()){
                    user = new User(rs.getInt("acc_id"), rs.getInt("acc_type"), rs.getString("username"),
                            rs.getString("password"), rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("job"), rs.getInt("productivity"));
                }
            } catch (SQLException | NullPointerException ex){
                ex.printStackTrace();
                System.exit(1);
            }finally{
                try{
                    rs.close();
                    st.close();
                    disconnect(c);
                } catch(Exception ex){
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user){ listener.onGetUsers(user);}
    }

    public static void fulfillPallet() { new fulfillPalletAsyncTask().execute(); }
    private static class fulfillPalletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...params){
            StringBuilder command = new StringBuilder();

            Statement st = null;
            String cmd = "update pallet set fulfilled = 1 where pallet_id = " + curr_id + ";";
            command.append(cmd);
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
    private static class AuthenticateAsyncTask extends AsyncTask<Object, Void, ArrayList<String>> {
        String username;
        String password;
        OnAuthenticationListener listener;

        @Override
        protected ArrayList<String> doInBackground(Object... params) {    //FIXME: SCRUB USERNAME OF SPECIAL CHARACTERS BEFORE CHECKING
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
            String acc_type = "false", correctPass = "";
            ArrayList<String> retVal = new ArrayList<>();
            retVal.add(acc_type);
            retVal.add("");

            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                if (!rs.next()) {
                    rs.close();
                    return retVal;
                }
                if (rs.getInt("acc_type") == 0) acc_type = "true";
                retVal.set(1, rs.getString("job").toLowerCase());
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
            if (password.equals(correctPass)) retVal.set(0, acc_type);
            return retVal;
        }
        @Override
        protected void onPostExecute(ArrayList<String> retVal) { listener.onAuthentication(retVal.get(0).equals("true"), retVal.get(1)); }
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