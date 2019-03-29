package edu.psu.sjt5370.palletization;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "jdbc:jtds:sqlserver://mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com:1433;databaseName=warehouse;user=masterUser;password=master1234;sslProtocol=TLSv1";
    private static final int MAX_PALLETS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FIXME: Display resulting palletization
    }



    interface OnPalletizedListener { void onPalletized(ArrayList<ArrayList<Product>> pallets); }
    public static void palletize(int routeNumber, OnPalletizedListener listener) { new PalletizeAsyncTask().execute(routeNumber, listener); }
    private static class PalletizeAsyncTask extends AsyncTask<Object, Void, ArrayList<ArrayList<Product>>> {
        OnPalletizedListener listener;
        int routeNum;
        @Override
        protected ArrayList<ArrayList<Product>> doInBackground(Object... params) {
            routeNum = (Integer) params[0];
            listener = (OnPalletizedListener) params[1];
            ArrayList<ArrayList<Product>> pallets = new ArrayList<>();
            String command1 = "select * from route_info where route_id = " + routeNum + ";";
            Statement st = null;
            ResultSet rs = null;
            Connection c = connect();
            try {
                st = c.createStatement();
                rs = st.executeQuery(command1);
                int[] orderNums = new int[] {};
                int[] stopNums = new int[] {};
                for (int i = 0; rs.next(); i++) {
                    orderNums[i] = rs.getInt("order_num");
                    stopNums[i] = rs.getInt("stop_num");
                }
                rs.close();
                st.close();
                ArrayList<Order> orders = new ArrayList<>();

                for(int i = 0; i < orderNums.length; i++) {
                    String command2 = "select prod_id, quantity from order_full, order_item where order_full.order_num = order_item.order_num and order_full.order_num = " + orderNums[i] + ";";
                    st = c.createStatement();
                    rs = st.executeQuery(command2);
                    Order order = new Order();
                    order.orderNum = orderNums[i];
                    order.stopNum = stopNums[i];
                    while(rs.next()) {
                        order.prodIDs.add(rs.getInt("prod_id"));
                        order.quantities.add(rs.getInt("quantity"));
                    }
                    orders.add(order);
                    rs.close();
                    st.close();
                }
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return o2.stopNum - o1.stopNum;
                    }
                });                                                     // Orders now reverse sorted by stops

                //FIXME: Palletize according to written algorithm

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
            return pallets;
        }

        @Override
        protected void onPostExecute(ArrayList pallets) { listener.onPalletized(pallets); }
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
