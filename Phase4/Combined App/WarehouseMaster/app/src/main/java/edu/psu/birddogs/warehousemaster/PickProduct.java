package edu.psu.birddogs.warehousemaster;

class PickProduct {
    private int id;
    private String name;
    private String desc;
    private String manu;
    private int priority;
    private int quantity;
    private boolean scanned;

    public PickProduct(){
        scanned = false;
    }

    public void setID(int prod_id) {
        this.id = prod_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.desc = description;
    }

    public void setManu(String manu){this.manu = manu;}

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setScanned(boolean scanned) { this.scanned = scanned; }

    public int getID(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return desc;
    }
    public String getManu(){return manu;}
    public int getPriority(){
        return priority;
    }
    public int getQuantity(){
        return quantity;
    }
    public boolean getScanned() { return scanned; }
}
