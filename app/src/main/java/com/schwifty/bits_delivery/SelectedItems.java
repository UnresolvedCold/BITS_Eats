package com.schwifty.bits_delivery;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectedItems
{
    private String name;
    private String UID;
    private float price;
    private int nItems;
    private Boolean isPackagable;
    private float packingPrice;

    public Boolean isEqual(SelectedItems itm)
    {
        if(UID.equals(itm.getUID()))
        {
            return true;
        }
        return false;
    }

    public SelectedItems(String UID,String name, float price, int nItems,Boolean isPAckagable,float packingPrice) {
        Log.d("SelectedItems","Constructor : packagingPrice : "+packingPrice);
        this.UID = UID;
        this.name=name;
        this.price = price;
        this.nItems = nItems;
        this.isPackagable=isPAckagable;
        this.packingPrice=packingPrice;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getnItems() {
        return nItems;
    }

    public void setnItems(int nItems) {
        this.nItems = nItems;
    }

    public Boolean IsPackagable()
    {
        return isPackagable;
    }

    public float getPackingPrice() {
        return packingPrice;
    }
}


