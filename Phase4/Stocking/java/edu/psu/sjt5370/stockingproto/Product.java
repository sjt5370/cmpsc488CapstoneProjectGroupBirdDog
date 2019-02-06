package edu.psu.sjt5370.stockingproto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Product implements Parcelable {
    int bulkStock;
    int shelfStock;
    String name;
    static int count = 0;

    public Product() {
        Random rand = new Random();
        bulkStock = rand.nextInt(1000) + 1;
        shelfStock = 0;
        name = "Example Product " + ++count;
    }
    private Product(Parcel parcel) {
        bulkStock = parcel.readInt();
        shelfStock = parcel.readInt();
        name = parcel.readString();
    }

    public boolean equals(Product that) { return this.name.equals(that.name); }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bulkStock);
        out.writeInt(shelfStock);
        out.writeString(name);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) { return new Product(in); }
        public Product[] newArray(int size) { return new Product[size]; }
    };
}
