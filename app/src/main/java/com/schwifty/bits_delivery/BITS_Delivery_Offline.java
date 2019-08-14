package com.schwifty.bits_delivery;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class BITS_Delivery_Offline extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
