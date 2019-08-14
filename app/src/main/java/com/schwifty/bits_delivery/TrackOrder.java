package com.schwifty.bits_delivery;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.schwifty.bits_delivery.UTILS.Ads;

import org.codechimp.apprater.AppRater;

import java.util.Random;

import static android.view.View.GONE;

public class TrackOrder extends AppCompatActivity {

    DatabaseReference trackRef_Delivery, trackRef_NonDelivery;
    RecyclerView deliveryList,nonDeliveryList;

    FirebaseRecyclerAdapter<model,holder> adapterForDelivery, adapterForNonDelivery;

    String android_id ;

    LayoutInflater inflater;

    private static Boolean shouldntAdBeShown = false;
    private static int rnd;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        if(!shouldntAdBeShown)
            InitAd();


        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        trackRef_Delivery = FirebaseDatabase.getInstance().getReference().child("TrackOrder");
        trackRef_NonDelivery = FirebaseDatabase.getInstance().getReference().child("TrackNonDeliveryOrders");

        deliveryList = findViewById(R.id.track_Delivery);
        nonDeliveryList = findViewById(R.id.track_NonDelivery);

        ViewCompat.setNestedScrollingEnabled(deliveryList, false);
        ViewCompat.setNestedScrollingEnabled(nonDeliveryList, false);

        AppRater.app_launched(this);
        AppRater.setNumLaunchesForRemindLater(10);
        AppRater.setVersionNameCheckEnabled(true);

        inflater = LayoutInflater.from(TrackOrder.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        trackRef_Delivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RunDeliveryUpdates();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        trackRef_NonDelivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RunNonDeliveryUpdates();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RunDeliveryUpdates() {
         Query query = trackRef_Delivery.orderByChild("deviceId").startAt(android_id).endAt(android_id);
         AttachAdapter(query,
                deliveryList,
                adapterForDelivery
                );
    }

    private void RunNonDeliveryUpdates() {
        Query query = trackRef_NonDelivery.orderByChild("deviceId").startAt(android_id).endAt(android_id);
        AttachAdapter(query,
                nonDeliveryList,
                adapterForNonDelivery
        );
    }

    private void AttachAdapter (Query query, RecyclerView itemList, FirebaseRecyclerAdapter<model,holder> adapter)
    {
        FirebaseRecyclerOptions<model> option = new FirebaseRecyclerOptions.Builder<model>().setQuery(
                query,
                new SnapshotParser<model>() {
                    @NonNull
                    @Override
                    public model parseSnapshot(@NonNull DataSnapshot snapshot)
                    {

                        if(snapshot.hasChild("Cost")
                            &&snapshot.hasChild("status")
                        )
                        {
                            return new model(
                                    snapshot.child("Cost").getValue().toString(),
                                    snapshot.child("mode").getValue().toString(),
                                    snapshot.child("OrderUID").getValue().toString(),
                                    snapshot.child("status").getValue().toString(),
                                    snapshot.child("token").getValue().toString(),
                                    snapshot.child("isPaid").getValue().toString(),
                                    snapshot.child("Mess").getValue().toString()
                            );
                        }
                        else {
                            return new model("loading", "loading", "loading", "loading", "loading","loading","loading");
                        }

                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<model, holder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull model model)
            {

                holder.setCost(model.getCost());
                holder.setItems(model.getOrderUId());
                holder.setStatus(model.getStatus(),model.getMode(),model.getMess());
                holder.setMode(model.getMode());
                holder.setToken(model.getToken());
                holder.setmView(model.getIsPaid(),model.getStatus());

            }

            @NonNull
            @Override
            public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_track_order, viewGroup, false);

                return new holder(view);
            }
        };

        itemList.setLayoutManager(new LinearLayoutManager(TrackOrder.this));
        itemList.setAdapter(adapter);

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapterForDelivery!=null)
        adapterForDelivery.stopListening();

        if(adapterForNonDelivery!=null)
            adapterForNonDelivery.stopListening();
    }

    //For inflating the items user bought
    private View InflateItemsView(String name, String qty, LinearLayout linearLayout) {

        TextView _vName,_vCount;

        View view = inflater.inflate(R.layout.template_track_order_items, null, false);
        _vName=view.findViewById(R.id.template_track_order_name);
        _vCount=view.findViewById(R.id.template_track_order_qty);


        _vName.setText(name);
        _vCount.setText(qty);


        linearLayout.addView(view);
        return view;
    }


    //Ads
    InterstitialAd mInterstitialAd;

    private void InitAd() {

        MobileAds.initialize(this, Ads.AppId);

        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId(Ads.AdId);

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                 //Code to be executed when an ad finishes loading.

                 ShowAd();
            }

            @Override
            public void onAdClosed() {

                Random rand = new Random();
                rnd = rand.nextInt(50);

                Log.d("Random_",rnd+"");

                if(rnd%2 == 0)
                {
                    shouldntAdBeShown =true;
                }

            }
        });

    }

    private void ShowAd() {

        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    //Model of Order
    class model
    {
        String Cost, mode, OrderUId, status ,token,isPaid,mess;

        public model() {
        }

        public model(String cost, String mode, String orderUId, String status, String token, String isPaid,String mess) {
            Cost = cost;
            this.mode = mode;
            OrderUId = orderUId;
            this.status = status;
            this.token = token;
            this.isPaid = isPaid;
            this.mess = mess;
        }

        public String getMess() {
            return mess;
        }

        public void setMess(String mess) {
            this.mess = mess;
        }

        public String getCost() {
            return Cost;
        }

        public void setCost(String cost) {
            Cost = cost;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getOrderUId() {
            return OrderUId;
        }

        public void setOrderUId(String orderUId) {
            OrderUId = orderUId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getIsPaid() {
            return isPaid;
        }

        public void setIsPaid(String isPaid) {
            this.isPaid = isPaid;
        }
    }

    class holder extends RecyclerView.ViewHolder
    {

        TextView v_Cost,v_Token,v_Status,v_Mode;
        LinearLayout v_Items;
        View mView;
        public holder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            v_Cost = mView.findViewById(R.id.template_track_order_cost);
            v_Token = mView.findViewById(R.id.template_track_order_token);
            v_Status = mView.findViewById(R.id.template_track_order_status);
            v_Mode = mView.findViewById(R.id.template_track_order_mode);

            v_Items = mView.findViewById(R.id.template_track_order_Items);

        }

        public void setmView(String isPaid,String status)
        {
            if(
                    !isPaid.equals("true")
            )
            {
                mView.setVisibility(GONE);
                mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            if(status.equals("new")||status.equals("pro")||status.equals("end")||status.equals("can"))
            {

            }
            else
            {
                mView.setVisibility(GONE);
                mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

            }
        }

        public void setCost(String cost)
        {
            v_Cost.setText("Total Price : "+cost);
        }

        public void setToken(String token)
        {
            v_Token.setText("Token : "+token);
        }

        public void setMode(String mode)
        {
            String value="";

            if(mode.equals("EatHere"))
            {
                value = "Dine-in";
            }

            if(mode.equals("TakeAway"))
            {
                value = "Pick-up";
            }

            if(mode.equals("Delivery"))
            {
                value = "Delivery";
            }

            v_Mode.setText(value);
        }
        public void setStatus(String status,String mode,String mess)
        {
            String value="";

            if(status.equals("new"))
            {
                value = "Your order has been received";
            }

            if(status.equals("pro"))
            {
                value = "Your order has been placed";
            }

            if(status.equals("end"))
            {

                if(mode.equals("Delivery"))
                {
                    value = "Your order is out for delivery";
                }
                else
                {
                    if(mess.equals("RC")||
                        mess.equals("FK"))
                    {
                        value = "Come and collect your order";
                    }
                    else {
                        value = "Your order is ready";
                    }
                }

            }

            if(status.equals("can"))
            {
                value = "Your order has been canceled due to unavailability of the item";
            }

            v_Status.setText(value);
        }

        public void setItems(String OrderUID)
        {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Order").child(OrderUID);

            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String mess = dataSnapshot.child("Mess").getValue().toString();

                    if(mess.length()==1) {
                        InflateItemsView("Mess : " + mess,"", v_Items);
                    }
                    else
                    {
                        //Name your eatery here
                        if(mess.equals("RC"))
                        {
                            mess="Red Chillis";
                        }
                        if(mess.equals("FK"))
                        {
                            mess="Food King";
                        }
                        InflateItemsView("Eatery : " + mess,"", v_Items);
                    }


                    InflateItemsView("Item","Qty",v_Items);

                    for (DataSnapshot itemSnapshot: dataSnapshot.child("Ordered Items").getChildren()) {
                        InflateItemsView(itemSnapshot.getKey().toString(),itemSnapshot.getValue().toString(),v_Items);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }
}
