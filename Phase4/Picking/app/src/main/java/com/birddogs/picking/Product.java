package com.birddogs.picking;

class Product {
    private int id;
    private String name;
    private String desc;
    private int priority;
    private int quantity;

    public void setID(int prod_id) {
        this.id = prod_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.desc = description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getID(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return desc;
    }
    public int getPriority(){
        return priority;
    }
    public int getQuantity(){
        return quantity;
    }
}
