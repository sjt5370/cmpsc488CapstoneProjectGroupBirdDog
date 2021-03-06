package edu.psu.birddogs.warehousemaster;

import android.os.Parcel;
import android.os.Parcelable;

public class StockProduct implements Parcelable {
    private Integer id;
    private String name;
    private String description;
    private String manufacturer;
    private Double price;
    private Integer priority;
    private Double volume;
    private Integer bulkStock;
    private Integer shelfStock;
    private boolean stockRequest;

    public StockProduct() { stockRequest = false; }
    private StockProduct(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        description = parcel.readString();
        manufacturer = parcel.readString();
        price = parcel.readDouble();
        priority = parcel.readInt();
        volume = parcel.readDouble();
        bulkStock = parcel.readInt();
        shelfStock = parcel.readInt();
        stockRequest = (parcel.readInt() != 0);
    }

    public Integer getID() { return id; }
    public String getProductName() { return name; }
    public String getDescription() { return description; }
    public String getManufacturer() { return manufacturer; }
    public Double getPrice() { return price; }
    public Integer getPriority() { return priority; }
    public Double getVolume() {return volume; }
    public Integer getBulkStock() { return bulkStock; }
    public Integer getShelfStock() { return shelfStock; }

    public void setID(int id) { this.id = id; }
    public void setProductName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setPrice(double price) { this.price = price; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setVolume(double volume) { this.volume = volume; }
    public void setBulkStock(int bulkStock) { this.bulkStock = bulkStock; }
    public void setShelfStock(int shelfStock) { this.shelfStock = shelfStock; }

    public boolean stockRequested() { return stockRequest; }
    public void requestStock() { stockRequest = true; }
    public void stock() { stockRequest = false; }

    public boolean equals(StockProduct that) { return this.id.equals(that.getID()); }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(description);
        out.writeString(manufacturer);
        out.writeDouble(price);
        out.writeInt(priority);
        out.writeDouble(volume);
        out.writeInt(bulkStock);
        out.writeInt(shelfStock);
        out.writeInt(stockRequest ? 1 : 0);
    }

    public static final Parcelable.Creator<StockProduct> CREATOR = new Parcelable.Creator<StockProduct>() {
        public StockProduct createFromParcel(Parcel in) { return new StockProduct(in); }
        public StockProduct[] newArray(int size) { return new StockProduct[size]; }
    };
}
