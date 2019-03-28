package edu.psu.sjt5370.palletization;

public class Product{
    int id;
    String name;
    String description;
    String manufacturer;
    double price;
    int priority;
    double volume;
    int bulkStock;
    int shelfStock;
    boolean stockRequest;

    public Product() { stockRequest = false; }

    public boolean equals(Product that) { return this.id == that.id; }
}
