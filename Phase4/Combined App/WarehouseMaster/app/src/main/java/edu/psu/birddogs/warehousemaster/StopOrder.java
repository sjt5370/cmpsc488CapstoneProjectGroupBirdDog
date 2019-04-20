package edu.psu.birddogs.warehousemaster;

import android.os.Parcel;
import android.os.Parcelable;

public class StopOrder implements Parcelable {
    public int orderNum;
    public int stopNum;
    public boolean complete;

    public StopOrder() { complete = false; }

    public StopOrder(int orderNum, int stopNum) {
        this.orderNum = orderNum;
        this.stopNum = stopNum;
        complete = false;
    }

    private StopOrder(Parcel parcel) {
        orderNum = parcel.readInt();
        stopNum = parcel.readInt();
        complete = (parcel.readInt() != 0);
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(orderNum);
        out.writeInt(stopNum);
        out.writeInt(complete ? 1 : 0);
    }

    public static final Parcelable.Creator<StopOrder> CREATOR = new Parcelable.Creator<StopOrder>() {
        public StopOrder createFromParcel(Parcel in) { return new StopOrder(in); }
        public StopOrder[] newArray(int size) { return new StopOrder[size]; }
    };
}
