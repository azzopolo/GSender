package com.icanstudioz.taxicustomer.pojo;

import java.io.Serializable;

/**
 * Created by ${Ican} on 12/5/18.
 */
public class Offers implements Serializable {

    String id;
    String ride_id;
    String user_id;
    String driver_id;
    String Total;
    String amount;
    String pickup_adress;
    String drop_address;
    String PickupDate;


    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getPickupDate() {
        return PickupDate;
    }

    public void setPickup_adress(String pickup_adress) {
        this.pickup_adress = pickup_adress;
    }

    public String getPickup_adress() {
        return pickup_adress;
    }

    public void setPickupDate(String pickupDate) {
        PickupDate = pickupDate;
    }

    public Offers() {
    }

    public String getAmount() {
        return amount;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getTotal() {
        return Total;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
