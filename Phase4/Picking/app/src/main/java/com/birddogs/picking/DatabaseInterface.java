package com.birddogs.picking;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseInterface {

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
