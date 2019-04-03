package com.birddogs.picking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Pallet{
    private ArrayList<Product> products = new ArrayList<>();

    public Pallet(){ }

    public void addProduct(Product product){
        products.add(product);
    }
    public int getSize(){
        return products.size();
    }
    public int getID(int i){
        return products.get(i).getID();
    }
    public String getName(int i) {
        return products.get(i).getName();
    }
    public String getDescription(int i) {
        return products.get(i).getDescription();
    }
    public int getPriority(int i){
        return products.get(i).getPriority();
    }
    public int getQuantity(int i){
        return products.get(i).getQuantity();
    }

    public String toString(){
        String a = "";
        for(int i = 0; i < products.size(); i++){
            a += getID(i) + getName(i) + "\n";
        }
        return a;
    }
}
