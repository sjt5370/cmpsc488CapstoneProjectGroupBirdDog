package edu.psu.birddogs.warehousemaster;

import java.util.ArrayList;

public class Pallet{
    private ArrayList<PickProduct> pickProducts = new ArrayList<>();

    public Pallet(){ }

    public void addProduct(PickProduct pickProduct){
        pickProducts.add(pickProduct);
    }
    public int getSize(){
        return pickProducts.size();
    }
    public int getID(int i){
        return pickProducts.get(i).getID();
    }
    public String getName(int i) {
        return pickProducts.get(i).getName();
    }
    public String getDescription(int i) {
        return pickProducts.get(i).getDescription();
    }
    public String getManu(int i) { return pickProducts.get(i).getManu(); }
    public int getPriority(int i){
        return pickProducts.get(i).getPriority();
    }
    public int getQuantity(int i){
        return pickProducts.get(i).getQuantity();
    }

    public String toString(){
        String a = "";
        for(int i = 0; i < pickProducts.size(); i++){
            a += getID(i) + getName(i) + "\n";
        }
        return a;
    }
}
