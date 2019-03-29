package com.icanstudioz.taxicustomer.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

/**
 * Created by android on 13/10/17.
 */

public class Pass implements Serializable,Parcelable {
    private Place fromPlace;
    private Place toPlace;
    private String driverId;
    private String driverName;
    private String fare;
    private String vehicleName;

    private String pick_up_date;
    private String truck_type;
    private String full_truck;
    private String llt_truck;
    private String drop_date;
    private String liting_date;
    private String note;

    private String pickupFullName;
    private String pickupPhone;
    private String pickupAdress;
    private String dropFullName;
    private String dropPhone;
    private String dropAddress;

    private String fullTotalW;
    private String fullTotalL;
    private String LLTPieceCount;
    private String LLTWight;
    private String LLTLenght;
    private String LLTHeight;
    private String LLTWeight;


    public String getFullTotalL() {
        return fullTotalL;
    }

    public void setFullTotalL(String fullTotalL) {
        this.fullTotalL = fullTotalL;
    }

    public String getFullTotalW() {
        return fullTotalW;
    }

    public void setFullTotalW(String fullTotalW) {
        this.fullTotalW = fullTotalW;
    }

    public String getLLTHeight() {
        return LLTHeight;
    }

    public void setLLTHeight(String LLTHeight) {
        this.LLTHeight = LLTHeight;
    }

    public String getLLTLenght() {
        return LLTLenght;
    }

    public void setLLTLenght(String LLTLenght) {
        this.LLTLenght = LLTLenght;
    }

    public String getLLTPieceCount() {
        return LLTPieceCount;
    }

    public void setLLTPieceCount(String LLTPieceCount) {
        this.LLTPieceCount = LLTPieceCount;
    }

    public String getLLTWeight() {
        return LLTWeight;
    }

    public void setLLTWeight(String LLTWeight) {
        this.LLTWeight = LLTWeight;
    }

    public String getLLTWight() {
        return LLTWight;
    }

    public void setLLTWight(String LLTWight) {
        this.LLTWight = LLTWight;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getDropFullName() {
        return dropFullName;
    }

    public void setDropFullName(String dropFullName) {
        this.dropFullName = dropFullName;
    }

    public String getDropPhone() {
        return dropPhone;
    }

    public void setDropPhone(String dropPhone) {
        this.dropPhone = dropPhone;
    }

    public String getPickupAdress() {
        return pickupAdress;
    }

    public void setPickupAdress(String pickupAdress) {
        this.pickupAdress = pickupAdress;
    }

    public String getPickupFullName() {
        return pickupFullName;
    }

    public void setPickupFullName(String pickupFullName) {
        this.pickupFullName = pickupFullName;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public void setPickupPhone(String pickupPhone) {
        this.pickupPhone = pickupPhone;
    }

    public String getPick_up_date() {
        return pick_up_date;
    }

    public void setPick_up_date(String pick_up_date) {
        this.pick_up_date = pick_up_date;
    }

    public String getDrop_date() {
        return drop_date;
    }

    public void setDrop_date(String drop_date) {
        this.drop_date = drop_date;
    }

    public String getFull_truck() {
        return full_truck;
    }

    public void setFull_truck(String full_truck) {
        this.full_truck = full_truck;
    }

    public String getLiting_date() {
        return liting_date;
    }

    public void setLiting_date(String liting_date) {
        this.liting_date = liting_date;
    }

    public String getLlt_truck() {
        return llt_truck;
    }

    public void setLlt_truck(String llt_truck) {
        this.llt_truck = llt_truck;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTruck_type() {
        return truck_type;
    }

    public void setTruck_type(String truck_type) {
        this.truck_type = truck_type;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public Pass() {
    }

    protected Pass(Parcel in) {
        driverId = in.readString();
        driverName = in.readString();
        fare = in.readString();
    }

    public static final Creator<Pass> CREATOR = new Creator<Pass>() {
        @Override
        public Pass createFromParcel(Parcel in) {
            return new Pass(in);
        }

        @Override
        public Pass[] newArray(int size) {
            return new Pass[size];
        }
    };

    public Place getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(Place fromPlace) {
        this.fromPlace = fromPlace;
    }

    public Place getToPlace() {
        return toPlace;
    }

    public void setToPlace(Place toPlace) {
        this.toPlace = toPlace;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(driverId);
        parcel.writeString(driverName);
        parcel.writeString(fare);
    }
}
