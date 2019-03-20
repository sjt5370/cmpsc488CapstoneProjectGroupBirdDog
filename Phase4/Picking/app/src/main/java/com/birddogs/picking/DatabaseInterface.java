package com.birddogs.picking;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DatabaseInterface {
    interface OnGetPalletListener { void onGetPallet(HashMap<Integer, Product> pallet);}
    interface OnGetUsersListener { void onGetUsers(User users);}

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "warehouse.db";
    private static final String URL = "jdbc:jtds:sqlserver://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433;databaseName=warehouse;user=masterUser;password=master1234;sslProtocol=TLSv1";
    private static final int PORT = 1433;
    private static final String USERNAME = "masterUser";
    private static final String PASSWORD = "master1234";
    public static int curr_id = 1;

    public static void getNewPallet(OnGetPalletListener listener) { new getNewPalletAsync().execute(listener);}
    private static class getNewPalletAsync extends AsyncTask<OnGetPalletListener, Void, HashMap<Integer, Product>>{
        OnGetPalletListener listener;

        @Override
        protected HashMap<Integer, Product> doInBackground(OnGetPalletListener... params){
            listener = params[0];
            StringBuilder command = new StringBuilder();
            command.append("select pallet.prod_id as prod_id, prod_name, prod_desc, manu, proirity, quantity ");
            command.append("from pallet, product ");
            command.append("where pallet.prod_id = product.prod_id and pallet.pallet_id = " + curr_id + ";");
            System.out.println(command.toString());

            HashMap<Integer, Product> pallet = new HashMap<>();
            Product product = null;
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try{
                st = c.createStatement();
                rs = st.executeQuery(command.toString());
                while(rs.next()){
                    product = new Product();
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
        protected void onPostExecute(HashMap<Integer, Product> pallet){ listener.onGetPallet(pallet);}
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
    public static void returnInventory(HashMap<Integer, Product> products) { new returnInventoryAsyncTask().execute(products); }
    private static class returnInventoryAsyncTask extends AsyncTask<HashMap<Integer, Product>, Void, Void> {
        HashMap<Integer, Product> prods;
        @Override
        protected Void doInBackground(HashMap<Integer, Product> ... params){
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
    public static void removePallet() { new removePalletAsyncTask().execute(); }
    private static class removePalletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...params){
            StringBuilder command = new StringBuilder();
            String cmd = "";

            Statement st = null;
            cmd = "delete from pallet where pallet_id = " + (curr_id - 1) + ";";
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

    private static Connection connect(){
        try{
            System.out.println("Connecting...");
            Connection c = DriverManager.getConnection(URL);
            System.out.println("Connected!");
            return c;
        } catch (SQLException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    private static void disconnect(Connection connection){
        System.out.println("Disconnecting..");
        try{
            connection.close();
        }catch (SQLException sqlex){
            sqlex.printStackTrace();;
            System.exit(1);

        }
        System.out.println("Disconnected!");
    }
}
