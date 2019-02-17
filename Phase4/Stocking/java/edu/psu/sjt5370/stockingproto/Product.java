package edu.psu.sjt5370.stockingproto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Product implements Parcelable {
    Integer id;
    String name;
    String description;
    String manufacturer;
    Double price;
    Integer bulkStock;
    Integer shelfStock;
    //static Integer count = 0;
    /*
    public Product() {
        Random rand = new Random();
        bulkStock = rand.nextInt(1000) + 1;
        shelfStock = 0;
        name = "Example Product " + ++count;
    }
    */
    public Product() {}
    private Product(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        description = parcel.readString();
        manufacturer = parcel.readString();
        price = parcel.readDouble();
        bulkStock = parcel.readInt();
        shelfStock = parcel.readInt();
    }

    public Integer getID() { return id; }
    public String getProductName() { return name; }
    public String getDescription() { return description; }
    public String getManufacturer() { return manufacturer; }
    public Double getPrice() { return price; }
    public Integer getBulkStock() { return bulkStock; }
    public Integer getShelfStock() { return shelfStock; }

    public void setID(int id) { this.id = id; }
    public void setProductName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setPrice(double price) { this.price = price; }
    public void setBulkStock(int bulkStock) { this.bulkStock = bulkStock; }
    public void setShelfStock(int shelfStock) { this.shelfStock = shelfStock; }

    public boolean equals(Product that) { return this.name.equals(that.name); }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(description);
        out.writeString(manufacturer);
        out.writeDouble(price);
        out.writeInt(bulkStock);
        out.writeInt(shelfStock);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) { return new Product(in); }
        public Product[] newArray(int size) { return new Product[size]; }
    };
}
