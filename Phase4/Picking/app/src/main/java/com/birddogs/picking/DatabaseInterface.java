package com.birddogs.picking;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseInterface {
    interface OnGetPalletListener { void onGetPallet(Pallet pallet);}

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "warehouse.db";
    private static final String URL = "jdbc:jtds:sqlserver://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433;databaseName=warehouse;user=masterUser;password=master1234;sslProtocol=TLSv1";
    private static final int PORT = 1433;
    private static final String USERNAME = "masterUser";
    private static final String PASSWORD = "master1234";
    public static int curr_id = 1;

    public static void getNewPallet(OnGetPalletListener listener) { new getNewPalletAsync().execute(listener);}
    private static class getNewPalletAsync extends AsyncTask<OnGetPalletListener, Void, Pallet>{
        OnGetPalletListener listener;

        @Override
        protected Pallet doInBackground(OnGetPalletListener... params){
            listener = params[0];
            StringBuilder command = new StringBuilder();
            command.append("select pallet.prod_id as prod_id, prod_name, prod_desc, proirity, quantity ");
            command.append("from pallet, product ");
            command.append("where pallet.prod_id = product.prod_id and pallet.pallet_id = " + curr_id + ";");
            System.out.println(command.toString());


            Pallet pallet = new Pallet();
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
                    product.setPriority(rs.getInt("proirity"));
                    product.setQuantity(rs.getInt("quantity"));
                    pallet.addProduct(product);
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
        protected void onPostExecute(Pallet pallet){ listener.onGetPallet(pallet);}
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




    static ArrayList<String> products;
    public static ArrayList getNewPalette(){
        //get next palette in queue
        products = new ArrayList();
        dummyData();
        return products;
    }

    public static HashMap<String, String> getUsers(){
        //returns a HashMap containing employee IDs and passwords
        HashMap<String, String> users = new HashMap<>();
        users.put("john", "fleming");
        return users;
    }

    public static void refreshStock(String product){
        //find product in database and decrement shelf stock
    }

    public static void cancelPalette(ArrayList<String> products){
        //replaces shelf stock for already scanned products
        //returns palette to order queue
    }
    private static void dummyData(){
        for(int i = 0; i < 20; i++){
            products.add("Product " + i);
        }
    }
}
