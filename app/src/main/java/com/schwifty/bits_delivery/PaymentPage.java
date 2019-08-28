package com.schwifty.bits_delivery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.schwifty.bits_delivery.UTILS.JSONParser;
import com.schwifty.bits_delivery.UTILS.Loader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PaymentPage extends AppCompatActivity {

    private static String ADMOB_APP_ID = "ca-app-pub-8704649037925552~2166036448";
    LayoutInflater inflater;

    private String TotalCost;
    private String UID;
    private String HostelRoom;
    private Boolean isPackagable;

    private String mess;

    private Toolbar mToolbar;
    private View vPayBtn_InstaMojo;

    private EditText vName, vEmail, vPhone, vRoom, vMob,vNPeople;
    private Spinner vHostel;
    private TextView vAmount;
    private LinearLayout lItemsHolderVerti;

    private String items;

    private String email, phone, name, purpose, mobile;

    String _hostel;
    String _room;
    String _name;
    String _mobile;
    String _NPeople;

    private DatabaseReference orderRef;
    private DatabaseReference trackRef;

    View v_DELIVERYCOST;
    View v_TOTALCOST;

    private static Boolean isPaymentSuccessful=false;

    private static String PAY_ID ="callofduty91298@okicici";
  // private static final String PAY_ID ="coldboy1998@oksbi";

    float deliveryCost;

    private static float THRESHOLD_FOR_PERCENT = 150f;
    private static float PERCENT = 0.1f;
    private String _TotalCost;

    private RadioGroup optionsGroup;
    private RadioButton EatHere, TakeAway, Delivery;

    TextView v_Summary;

    String messADelivery="closed",messANonDelivery="closed",
            messCDelivery="closed", messCNonDelivery="closed",
            messDDelivery="closed", messDNonDelivery="closed",
            RCEatHere="closed",RCTakeAway="closed",
            INSEatHere="closed",INSTakeAway="closed",
            INSDelivery="closed",INSNonDelivery="closed",
            fkEatHere = "closed",fkTakeAway="closed";

    private View vPayBtn_Delivery,vPayBtn_TakeAway, vPayBtn_EatHere, vPayBtn_NonDelivery;

    private String android_id;
    private String type_short="EH";
    private String token="0";
    private String tokenId="0";

    List<Items> itemsList;

    int OrderType=1; //0 = EatHere, 1 = take away 2 = delivery

    String currTime;
    int currTimeInt;

    int del_type=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_page);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                PaymentPage.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        tokenId = instanceIdResult.getToken();
                    }
                }
        );

        inflater = LayoutInflater.from(PaymentPage.this);

        mToolbar = findViewById(R.id.pay_appbar);
        setSupportActionBar(mToolbar);

        TotalCost = getIntent().getStringExtra("TotalCost");
        _TotalCost = TotalCost;
        UID = getIntent().getStringExtra("UID");
        // HostelRoom = getIntent().getStringExtra("HostelRoom");
        HostelRoom = "AH 1-103";
        items = getIntent().getStringExtra("items");
        mess = getIntent().getStringExtra("Mess");

        isPackagable = getIntent().getBooleanExtra("isPackagable", true);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Order").child(UID);
        trackRef = FirebaseDatabase.getInstance().getReference().child("TrackOrder");

        vName = findViewById(R.id.pay_Name);
        //vEmail=findViewById(R.id.pay_Email);
        //vPhone=findViewById(R.id.pay_Phone);
        vAmount = findViewById(R.id.pay_Cost);
        vRoom = findViewById(R.id.payment_page_RoomNo);
        vMob = findViewById(R.id.pay_Mob);
        vHostel = findViewById(R.id.pay_Hostel);
        lItemsHolderVerti = findViewById(R.id.pay_summary_items);
        vPayBtn_InstaMojo = findViewById(R.id.pay_PayBtn_InstaMojo);

        vPayBtn_Delivery = findViewById(R.id.pay_PayBtn_Delivery);
        vPayBtn_NonDelivery = findViewById(R.id.pay_PayBtn_NonDelivery);
        vPayBtn_TakeAway = findViewById(R.id.pay_PayBtn_TakeAway);
        vPayBtn_EatHere = findViewById(R.id.pay_PayBtn_EatHere);

        v_Summary = findViewById(R.id.payment_page_summary);

        itemsList=new ArrayList<>();

        purpose = mess+" "+type_short;
        vAmount.setText("Total Cost : Rs. " + TotalCost);

        vHostel.setOnItemSelectedListener(updateDeliveryCost);

        HideReceipt();

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //Init Radio Group
        optionsGroup = findViewById(R.id.pay_Options);
        EatHere = findViewById(R.id.pay_options_eatHere);
        TakeAway = findViewById(R.id.pay_options_takeAway);
        Delivery = findViewById(R.id.pay_options_Delivery);

        optionsGroup.setOnCheckedChangeListener(optionsChanged);

        StartListnerForMeshVariables();

        vPayBtn_EatHere.setVisibility(GONE);
        vPayBtn_Delivery.setVisibility(GONE);
        vPayBtn_TakeAway.setVisibility(GONE);
        v_Summary.setVisibility(GONE);

        vPayBtn_EatHere.setOnClickListener(payEatHere);
        vPayBtn_TakeAway.setOnClickListener(payTakeAway);
        vPayBtn_Delivery.setOnClickListener(payDelivery);


        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("HH");
        currTime =sdf.format(cal.getTime());
        currTimeInt = Integer.parseInt(currTime);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){

                currTime =sdf.format(cal.getTime());
                currTimeInt = Integer.parseInt(currTime);


                handler.postDelayed(this, delay);
            }
        }, delay);

        PopulateItemsList(items);
        __FinalCost=Float.toString(getInitialPrice(false));

      //  UpdateItemsView(itemsList,lItemsHolderVerti,false,1);

        generateToken();

        AutoFill();


    }

    private void AutoFill()
    {
        final EditText vRoom =findViewById(R.id.payment_page_RoomNo);
        EditText vName =findViewById(R.id.pay_Name);
        EditText vContact = findViewById(R.id.pay_Mob);
        final Spinner _s = findViewById(R.id.pay_Hostel);
        final String [] hostels = new String[]
                {
                        "AH1","AH2","AH3","AH4","AH5","AH6","AH7","AH8","AH9",
                        "CH1","CH2","CH3","CH4","CH5","CH6","CH7"
                };


        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            try {
                vName.setText(user.getDisplayName());

                if(TextUtils.isEmpty(user.getPhoneNumber()))
                vContact.setText(user.getEmail());
                else vContact.setText(user.getPhoneNumber());

                FirebaseDatabase.getInstance().getReference()
                        .child("StudentList")
                        .orderByChild("Email")
                        .equalTo(user.getEmail())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.getChildren().iterator().hasNext())
                                {
                                    DataSnapshot s =dataSnapshot.getChildren().iterator().next();
                                    vRoom.setText(s.child("Room").getValue().toString());
                                    String hostel = s.child("Hostel").getValue().toString();

                                    for(int i=0;i<hostels.length;i++)
                                    {
                                        if(hostel.contains(hostels[i]))_s.setSelection(i);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void generateToken()
    {

        DatabaseReference srchToken = FirebaseDatabase.getInstance().getReference()
                .child("TrackNonDeliveryOrders");

        srchToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int randomNo = (int)(Math.random() * 9 + 1);

                token  = ""+dataSnapshot.getChildrenCount()+randomNo;

                Log.e("merchant_",token + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void UpdateDeliveryCost(String hostel, boolean updateScheme)
    {
        //Update Delivery cost
        float totalCost=0f;

        deliveryCost = getDeliveryCost(hostel,updateScheme,del_type);

        totalCost = Float.parseFloat(_TotalCost)+deliveryCost;


        float tc_temp = Float.parseFloat(_TotalCost);
        if(totalCost>=THRESHOLD_FOR_PERCENT)
        {

            float exc_delivery = deliveryCost;
            deliveryCost=tc_temp*PERCENT;

        }

        deliveryCost +=getCrossDeliveryCost(hostel);
        totalCost=tc_temp+deliveryCost;

        deliveryCost = Math.round(deliveryCost*100.0f)/100.0f;
        totalCost = Math.round(totalCost*100.0f)/100.0f;

        TextView tv_dc = v_DELIVERYCOST.findViewById(R.id.template_bill_price);
        TextView tv_tc = v_TOTALCOST.findViewById(R.id.template_bill_price);

        tv_dc.setText(deliveryCost+"");
        tv_tc.setText(totalCost+"");

        TotalCost=totalCost+"";

        __FinalCost=TotalCost;


    }



    private void PopulateItemsList(String itmsStr) {

        String[] indiv_data = itmsStr.split("=");

        for (int i = 0; i < indiv_data.length; i++) {
            String[] collective = indiv_data[i].split("_");

            itemsList.add(new Items(collective[0], collective[1], collective[2], collective[3]));
        }

    }

    private float getInitialPrice(Boolean withPkg)
    {
        float sum =0f;

        if(itemsList.size()<=0)
        {
            return 0f;
        }

        if(withPkg)
        {

            for (Items next : itemsList) {

                sum+=(Float.parseFloat(next.getPrice())+Float.parseFloat(next.getPkgPrice()));
            }

        }
        else
        {
            for (Items next : itemsList) {

                sum+=(Float.parseFloat(next.getPrice()));
            }
        }

        return sum;

    }

    private void UpdateReceiptItemsView(String itmsStr,LinearLayout layout ,Boolean isHidden,String deliveryCost,String totalCost,int scheme) {

        InflateItemsView("Item", "Qty", "Price", "Pkg", layout,scheme);

        String[] indiv_data = itmsStr.split("=");


        for (int i = 0; i < indiv_data.length; i++) {
            String[] collective = indiv_data[i].split("_");
            //InflateItemsView("q","q","q","q",lItemsHolderVerti);
            View v=InflateItemsView(collective[0], collective[1], collective[2], collective[3], layout,scheme);

            if(isHidden)
            {
                v.setVisibility(GONE);
            }

        }

        v_DELIVERYCOST = InflateItemsView("Delivery cost", "", deliveryCost, "", layout,scheme);

        v_TOTALCOST = InflateItemsView("Total Cost", "", __FinalCost + "", "", layout,scheme);

    }


    //Get status of variables from the server
    private void StartListnerForMeshVariables()
    {
        DatabaseReference varRef = FirebaseDatabase.getInstance().getReference().child("Variables").child("Allowance");
        varRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                messADelivery = dataSnapshot.child("MessADelivery").getValue().toString();
                messANonDelivery = dataSnapshot.child("MessANonDelivery").getValue().toString();
                messCDelivery = dataSnapshot.child("MessCDelivery").getValue().toString();
                messCNonDelivery = dataSnapshot.child("MessCNonDelivery").getValue().toString();
                messDDelivery = dataSnapshot.child("MessDDelivery").getValue().toString();
                messDNonDelivery = dataSnapshot.child("MessDNonDelivery").getValue().toString();
                RCTakeAway=dataSnapshot.child("RCTakeAway").getValue().toString();
                RCEatHere=dataSnapshot.child("RCEatHere").getValue().toString();
                INSTakeAway=dataSnapshot.child("INSTakeAway").getValue().toString();
                INSEatHere=dataSnapshot.child("INSEatHere").getValue().toString();
                INSDelivery=dataSnapshot.child("INSDelivery").getValue().toString();
                INSNonDelivery = dataSnapshot.child("INSNonDelivery").getValue().toString();
                fkEatHere=dataSnapshot.child("FKEatHere").getValue().toString();
                fkTakeAway = dataSnapshot.child("FKTakeAway").getValue().toString();

                //Disable Payments if service in unavailable
                if(mess.equals("A"))
                {
                    if(Delivery.isChecked())
                    {

                        if (!messADelivery.equals("opened")) {
                            Delivery.setOnClickListener(serviceNotAvailable);
                            vPayBtn_Delivery.setVisibility(GONE);
                        } else {
                            Delivery.setEnabled(true);
                            vPayBtn_Delivery.setVisibility(VISIBLE);
                        }

                    }

                    if (!messANonDelivery.equals("opened")) {
                        EatHere.setOnClickListener(serviceNotAvailable);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                        vPayBtn_NonDelivery.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        EatHere.setEnabled(true);
                        if(TakeAway.isChecked())
                        TakeAway.setEnabled(true);
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                    }
                }


                if(mess.equals("C"))
                {
                    if(Delivery.isChecked()) {
                        if (!messCDelivery.equals("opened")) {
                            Delivery.setOnClickListener(serviceNotAvailable);
                            vPayBtn_Delivery.setVisibility(GONE);
                        } else {
                            Delivery.setEnabled(true);
                            vPayBtn_Delivery.setVisibility(VISIBLE);
                        }
                    }

                    if (!messCNonDelivery.equals("opened")) {
                        EatHere.setOnClickListener(serviceNotAvailable);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                        vPayBtn_NonDelivery.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        EatHere.setEnabled(true);
                        if(TakeAway.isChecked())
                        TakeAway.setEnabled(true);
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                    }
                }


                if(mess.equals("D"))
                {

                    if(Delivery.isChecked()) {
                        if (!messDDelivery.equals("opened")) {
                            Delivery.setOnClickListener(serviceNotAvailable);
                            vPayBtn_Delivery.setVisibility(GONE);
                        } else {
                            Delivery.setEnabled(true);
                            vPayBtn_Delivery.setVisibility(VISIBLE);
                        }
                    }

                    if (!messDNonDelivery.equals("opened")) {
                        EatHere.setOnClickListener(serviceNotAvailable);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                        vPayBtn_NonDelivery.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        EatHere.setEnabled(true);
                        if(TakeAway.isChecked())
                        TakeAway.setEnabled(true);
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                    }
                }


                if(mess.equals("RC"))
                {

                    Delivery.setVisibility(GONE);

                    if(!RCEatHere.equals("opened"))
                    {
                            vPayBtn_EatHere.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(VISIBLE);
                            vPayBtn_TakeAway.setVisibility(GONE);
                        }
                    }


                    if(!RCTakeAway.equals("opened"))
                    {
                            vPayBtn_TakeAway.setVisibility(GONE);
                    }
                    else
                    {
                        if(TakeAway.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(GONE);
                            vPayBtn_TakeAway.setVisibility(VISIBLE);
                        }
                    }

                }

                if(mess.equals("INS"))
                {
                    del_type = 1;

                    if(Delivery.isChecked()) {
                        if (!INSDelivery.equals("opened")) {
                            Delivery.setOnClickListener(serviceNotAvailable);
                            vPayBtn_Delivery.setVisibility(GONE);
                        } else {

                            if(getInitialPrice(false) >= 100.0) {

                                Delivery.setEnabled(true);
                                vPayBtn_Delivery.setVisibility(VISIBLE);
                            }

                            else
                            {
                                Delivery.setOnClickListener(deliveryNotAvailable);
                                vPayBtn_Delivery.setVisibility(GONE);
                            }
                        }
                    }

                    if (!INSNonDelivery.equals("opened")) {
                        EatHere.setOnClickListener(serviceNotAvailable);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                        vPayBtn_NonDelivery.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                            EatHere.setEnabled(true);
                        if(TakeAway.isChecked())
                            TakeAway.setEnabled(true);
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                    }

/*
                    Delivery.setVisibility(GONE);

                    if(!INSEatHere.equals("opened"))
                    {
                        vPayBtn_EatHere.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(VISIBLE);
                            vPayBtn_TakeAway.setVisibility(GONE);
                        }
                    }
*/

                    if(!INSTakeAway.equals("opened"))
                    {
                        vPayBtn_TakeAway.setVisibility(GONE);
                    }
                    else
                    {
                        if(TakeAway.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(GONE);
                            vPayBtn_TakeAway.setVisibility(VISIBLE);
                        }
                    }

                }

                if(mess.equals("FK"))
                {

                    Delivery.setVisibility(GONE);

                    if(!fkEatHere.equals("opened"))
                    {
                        vPayBtn_EatHere.setVisibility(GONE);
                    }
                    else
                    {
                        if(EatHere.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(VISIBLE);
                            vPayBtn_TakeAway.setVisibility(GONE);
                        }
                    }


                    if(!fkTakeAway.equals("opened"))
                    {
                        vPayBtn_TakeAway.setVisibility(GONE);
                    }
                    else
                    {
                        if(TakeAway.isChecked())
                        {
                            vPayBtn_EatHere.setVisibility(GONE);
                            vPayBtn_TakeAway.setVisibility(VISIBLE);
                        }
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void UpdateItemsView(List<Items> listItems,LinearLayout layout ,Boolean isHidden,int scheme) {
        //1=EatHere 2=TakeAway 3=Delivery

        if(((LinearLayout) layout).getChildCount() > 0)
            ((LinearLayout) layout).removeAllViews();

        InflateItemsView("Item", "Qty", "Price", "Pkg", layout,scheme);

        for(Items next : listItems)
        {
            View v=InflateItemsView(next.getName(),next.getQty(),next.getPrice(),next.getPkgPrice(), layout,scheme);

            if(isHidden)
            {
                v.setVisibility(GONE);
            }
        }


        v_DELIVERYCOST = InflateItemsView("Delivery cost", "", deliveryCost + "", "", layout,scheme);

        v_TOTALCOST = InflateItemsView("Total Cost", "", __FinalCost + "", "", layout,scheme);

        if(scheme==1||scheme==2)
        {
            v_DELIVERYCOST.setVisibility(GONE);
        }


        }


    @Override
    protected void onResume() {
        super.onResume();
        vPayBtn_EatHere.clearAnimation();
        vPayBtn_TakeAway.clearAnimation();
        vPayBtn_Delivery.clearAnimation();
    }

    @Override
    public void onBackPressed() {
        if(!isPaymentSuccessful) {
            super.onBackPressed();
        }

        else
        {
            Intent intent = new Intent(PaymentPage.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final Loader _l = new Loader("",R.layout.loading,this,false);

        if (data != null) //Result from google API
        {

            if (data.getStringExtra("response").toLowerCase().contains("SUCCESS".toLowerCase()))
            {

                if(OrderType==1||OrderType==2)
                {
                    trackOrderUID.child("isPaid").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            CloseDetails();
                            ShowReceipt(
                                    trackOrderUID,
                                    OrderType);

                            isPaymentSuccessful=true;

                        }
                    });
                }
                else
                {
                    trackRef.child(_hostel + "_" + _room+"="+rnd).child("isPaid").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CloseDetails();
                            ShowReceipt(
                                    trackRef.child(_hostel + "_" + _room+"="+rnd),
                                    OrderType);

                            isPaymentSuccessful=true;

                        }
                    });
                }

            }

            else
            {
                Toast.makeText(this, "Payment Not Successful", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {

            // Toast.makeText(this, "Payment Not Successful", Toast.LENGTH_SHORT).show();
        }

        _l.getDialog().dismiss();

    }

    private View InflateItemsView(String name,String qty,String price,String pkgPrice,LinearLayout linearLayout,int scheme) {

        TextView _vName,_vPrice,_vPkgPrice,_vCount;

        View view = inflater.inflate(R.layout.template_bill_items, null, false);
        _vName=view.findViewById(R.id.template_bill_name);
        _vPrice=view.findViewById(R.id.template_bill_price);
        _vPkgPrice=view.findViewById(R.id.template_bill_pkgprice);
        _vCount=view.findViewById(R.id.template_bill_qty);

        _vName.setText(name);
        _vPrice.setText(price);
        _vCount.setText(qty);
        _vPkgPrice.setText(pkgPrice);

        if(scheme==1)
        {
            _vPkgPrice.setVisibility(View.INVISIBLE);
        }

        linearLayout.addView(view);
        return view;
    }


    private float getDeliveryCost(String hostel, Boolean updateScheme,int type) {


        //For Mess
        if(type == 0) {
            String[] B_1 = new String[]{"AH 2", "AH 3", "AH 7", "AH 8", "CH 3", "CH 4", "CH 5", "CH 7"};
            String[] B_2 = new String[]{"AH 1", "AH 4", "AH 5", "CH 1", "CH 2", "CH 6"};
            String[] B_3 = new String[]{"AH 6", "AH 9"};

            float price_B_1 = 10.0f;
            float price_B_2 = 13.0f;
            float price_B_3 = 15.0f;

            for (String aB_1 : B_1) {
                if (aB_1.equals(hostel)) {
                    PERCENT = 0.1f;
                    return price_B_1;
                }
            }

            for (String aB_2 : B_2) {
                if (aB_2.equals(hostel)) {
                    PERCENT = 0.12f;
                    return price_B_2;
                }
            }

            for (String aB_3 : B_3) {
                if (aB_3.equals(hostel)) {
                    PERCENT = 0.13f;
                    return price_B_3;
                }
            }
        }

        //for INS
        if(type == 1)
        {
            return 10.0f;
        }

        return 0.0f;
    }

    private float getCrossDeliveryCost(String hostel)
    {
        String side_mess,side_hostel;
        side_hostel = hostel.split(" ")[0];

        if(side_hostel.equals("AH")&&mess.equals("C"))
        {
            return 5f;
        }

        if(side_hostel.equals("CH")&&mess.equals("A"))
        {
            return 5f;
        }
        if(side_hostel.equals("AH")&&mess.equals("D"))
        {
            return 20f;
        }
        if(side_hostel.equals("CH")&&mess.equals("D"))
        {
            return 20f;
        }
        else
        {
            return  0f;
        }
    }

    private void CloseDetails()
    {
        ScrollView sv = findViewById(R.id.pay_Details);

        for ( int i = 0; i < sv.getChildCount();  i++ ){
            View view = sv.getChildAt(i);
            view.setVisibility(GONE);
        }
    }

    private void HideReceipt()
    {
        ScrollView sv = findViewById(R.id.pay_Receipt);

        for ( int i = 0; i < sv.getChildCount();  i++ ){
            View view = sv.getChildAt(i);
            view.setVisibility(GONE);
        }
    }

    private void ShowReceipt(DatabaseReference Ref ,int scheme)
    {
        ScrollView sv =findViewById(R.id.pay_Receipt);

        for ( int i = 0; i < sv.getChildCount();  i++ ){
            View view = sv.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }

        TextView vr_Name = findViewById(R.id.pay_Receipt_Name);
        TextView vr_Mobile = findViewById(R.id.pay_Receipt_Mob);
        TextView vr_Hostel = findViewById(R.id.pay_Receipt_Hostel);
        TextView vr_Room = findViewById(R.id.pay_Receipt_Room);
        final TextView vr_IsPaid = findViewById(R.id.pay_OrderConfirm);

        LinearLayout vr_ItemsDetails = findViewById(R.id.pay_Receipt_summary_items);

        vr_Name.setText("Name : "+_name);
        vr_Mobile.setText("Mob : "+_mobile);
        vr_Hostel.setText("Address : "+_hostel);
        vr_Room.setText(_room);

        TextView vr_TotalCost = v_TOTALCOST.findViewById(R.id.template_bill_price);
        TextView vr_DeliveryCost = v_DELIVERYCOST.findViewById(R.id.template_bill_price);

        UpdateReceiptItemsView(items,vr_ItemsDetails,false,
                vr_DeliveryCost.getText().toString(), vr_TotalCost.getText().toString(),scheme);

        final Loader l = new Loader("",R.layout.loading,this,false);
        l.getDialog().show();

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("isPaid").getValue().toString().equals("true"))
                {
                    Toast.makeText(PaymentPage.this, "Payment Successful", Toast.LENGTH_SHORT).show();

                    vr_IsPaid.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorOrderConfirmed, null));
                    vr_IsPaid.setText("Payment Successful\n Order Confirmed\n"+
                            "You can now view your order on the track order page.");
                    l.getDialog().dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                l.getDialog().dismiss();
            }
        });
    }


    //Final Logic using Strings

    String __InitialCost;
    String __DeliveryCost;
    String __FinalCost;

    private void InitVariablesWithPkgPrice()
    {
        __InitialCost = TotalCost;
    }


    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(PaymentPage.this);
        /**
         * Test mode: Defaults
         * Change merchant key on server
         */
        String orderId ="order1";
        String mid="IBQnqX11834928702006";
        String custid="cust1";
        String website="WEBSTAGING";

        String url ="https://paytmbitsdilevery.herokuapp.com/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        //String varifyurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderId;
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;

        Calendar cal = Calendar.getInstance();

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(999) + 100;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {

            /**
             * Init Production
             */
            //  mid="DHaxMq53341998853874";
            //  orderId = "order"+cal.hashCode()+""+randomInt;
            //  custid="cust"+cal.hashCode()+""+randomInt;
            //  website="DEFAULT";
            // varifyurl="https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderId;
            // varifyurl="https://paytmbitsdilevery.herokuapp.com/verifyChecksum.php?ORDERID="+orderId;
            varifyurl="https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp?ORDER_ID="+orderId;


            JSONParser jsonParser = new JSONParser(PaymentPage.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+TotalCost+"&WEBSITE="+website+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getProductionService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", TotalCost);
            paramMap.put("WEBSITE", website);
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "coldboy1998@gmail.com");   // no need
            //paramMap.put( "MOBILE_NO" , "7004219327");  // no need


            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);

            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(PaymentPage.this, true, true, new PaytmPaymentTransactionCallback() {
                /*Call Backs*/
                public void someUIErrorOccurred(String inErrorMessage) {
                    Toast.makeText(PaymentPage.this, "1", Toast.LENGTH_SHORT).show();
                }

                public void onTransactionResponse(Bundle inResponse) {
                    Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                }

                public void networkNotAvailable() {
                    Toast.makeText(PaymentPage.this, "3", Toast.LENGTH_SHORT).show();
                }

                public void clientAuthenticationFailed(String inErrorMessage) {
                    Toast.makeText(PaymentPage.this, "4", Toast.LENGTH_SHORT).show();
                }

                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                    Toast.makeText(PaymentPage.this, "5", Toast.LENGTH_SHORT).show();
                }

                public void onBackPressedCancelTransaction() {
                    Toast.makeText(PaymentPage.this, "6", Toast.LENGTH_SHORT).show();
                }

                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                    Toast.makeText(PaymentPage.this, "7", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void writeToFile(String data,String filename ,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            //Toast.makeText(context, ""+getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    AdapterView.OnItemSelectedListener updateDeliveryCost = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //Update Delivery cost
            float totalCost;


            deliveryCost = getDeliveryCost(adapterView.getItemAtPosition(i).toString(), true,del_type);
            deliveryCost+=getCrossDeliveryCost(adapterView.getItemAtPosition(i).toString());


            totalCost = Float.parseFloat(_TotalCost)+deliveryCost;

            if(totalCost>=THRESHOLD_FOR_PERCENT)
            {
                float tc_temp = Float.parseFloat(_TotalCost);
                deliveryCost=tc_temp*PERCENT;
                totalCost=tc_temp+deliveryCost;
            }

            deliveryCost = Math.round(deliveryCost*100.0f)/100.0f;
            totalCost = Math.round(totalCost*100.0f)/100.0f;

            TextView tv_dc = v_DELIVERYCOST.findViewById(R.id.template_bill_price);
            TextView tv_tc = v_TOTALCOST.findViewById(R.id.template_bill_price);

            tv_dc.setText(deliveryCost+"");
            tv_tc.setText(totalCost+"");

            TotalCost=totalCost+"";

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    //Radio Button updates
    RadioGroup.OnCheckedChangeListener optionsChanged = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i)
        {


            v_Summary.setVisibility(VISIBLE);

            if(i==R.id.pay_options_eatHere)
            {
                findViewById(R.id.pay_Address).setVisibility(GONE);
                __FinalCost=Float.toString(getInitialPrice(false));
                UpdateItemsView(itemsList,lItemsHolderVerti,false,1);

                type_short="EH";
                OrderType = 1;


                if(mess.equals("A"))
                {

                    if (!messANonDelivery.equals("opened"))
                    {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        EatHere.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_EatHere.setVisibility(VISIBLE);
                        EatHere.setOnClickListener(null);
                    }
                }

                if(mess.equals("C"))
                {
                    if (!messCNonDelivery.equals("opened"))
                    {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        EatHere.setOnClickListener(serviceNotAvailable);

                    }
                    else
                    {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_EatHere.setVisibility(VISIBLE);
                        EatHere.setOnClickListener(null);
                    }
                }

                if (mess.equals("D"))
                {
                    if (!messDNonDelivery.equals("opened"))
                    {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        EatHere.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_EatHere.setVisibility(VISIBLE);
                        EatHere.setOnClickListener(null);
                    }
                }

                if (mess.equals("RC"))
                {
                    PAY_ID="buntysgoa@okaxis";
                    findViewById(R.id.pay_NPeople).setVisibility(VISIBLE);

                    if (!RCEatHere.equals("opened"))
                    {
                        vPayBtn_EatHere.setVisibility(GONE);
                        EatHere.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_EatHere.setVisibility(VISIBLE);
                        EatHere.setOnClickListener(null);
                    }
                }

                if (mess.equals("INS"))
                {
                    del_type = 1;

                    FirebaseDatabase.getInstance().getReference()
                            .child("Variables")
                            .child("Payment")
                            .child("INS")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    PAY_ID = dataSnapshot.getValue().toString();
                                    findViewById(R.id.pay_NPeople).setVisibility(GONE);

                                    if (!INSEatHere.equals("opened"))
                                    {
                                        vPayBtn_EatHere.setVisibility(GONE);
                                        EatHere.setOnClickListener(serviceNotAvailable);
                                    }
                                    else
                                    {
                                        vPayBtn_EatHere.setVisibility(VISIBLE);
                                        EatHere.setOnClickListener(null);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }


                if (mess.equals("FK"))
                {
                    findViewById(R.id.pay_NPeople).setVisibility(GONE);
                    if (!fkEatHere.equals("opened"))
                    {
                        vPayBtn_EatHere.setVisibility(GONE);
                        EatHere.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_EatHere.setVisibility(VISIBLE);
                        EatHere.setOnClickListener(null);
                    }
                }


               // vPayBtn_EatHere.setVisibility(VISIBLE);

                vPayBtn_TakeAway.setVisibility(GONE);
                vPayBtn_Delivery.setVisibility(GONE);
            }

            if(i==R.id.pay_options_takeAway)
            {
                __FinalCost=Float.toString(getInitialPrice(true));
                UpdateItemsView(itemsList,lItemsHolderVerti,false,2);

                findViewById(R.id.pay_Address).setVisibility(GONE);

                type_short="TA";
                OrderType=2;

                if(mess.equals("A")) {
                    if (!messANonDelivery.equals("opened")) {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        TakeAway.setOnClickListener(serviceNotAvailable);

                    } else {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                        TakeAway.setOnClickListener(null);
                    }
                }
                else
                if(mess.equals("C")) {
                    if (!messCNonDelivery.equals("opened")) {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        TakeAway.setOnClickListener(serviceNotAvailable);

                    } else {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                        TakeAway.setOnClickListener(null);
                    }
                }
                else
                if(mess.equals("D")) {
                    if (!messDNonDelivery.equals("opened")) {
                        vPayBtn_NonDelivery.setVisibility(GONE);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                    } else {
                        vPayBtn_NonDelivery.setVisibility(VISIBLE);
                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                        TakeAway.setOnClickListener(null);
                    }
                }
                else
                if (mess.equals("RC"))
                {
                    PAY_ID = "buntysgoa@okaxis";
                    findViewById(R.id.pay_NPeople).setVisibility(GONE);
                    if (!RCTakeAway.equals("opened"))
                    {
                        vPayBtn_TakeAway.setVisibility(GONE);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                        TakeAway.setOnClickListener(null);
                    }
                }
                else
                if (mess.equals("INS"))
                {
                    del_type = 1;
                    FirebaseDatabase.getInstance().getReference()
                            .child("Variables")
                            .child("Payment")
                            .child("INS")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    PAY_ID = dataSnapshot.getValue().toString();
                                    findViewById(R.id.pay_NPeople).setVisibility(GONE);

                                    if (!INSTakeAway.equals("opened"))
                                    {
                                        vPayBtn_TakeAway.setVisibility(GONE);
                                        TakeAway.setOnClickListener(serviceNotAvailable);
                                    }
                                    else
                                    {
                                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                                        TakeAway.setOnClickListener(null);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
                else
                if(mess.equals("FK")) {
                    findViewById(R.id.pay_NPeople).setVisibility(GONE);
                    if (!fkTakeAway.equals("opened"))
                    {
                        vPayBtn_TakeAway.setVisibility(GONE);
                        TakeAway.setOnClickListener(serviceNotAvailable);
                    }
                    else
                    {
                        vPayBtn_TakeAway.setVisibility(VISIBLE);
                        TakeAway.setOnClickListener(null);
                    }
                }

              //  vPayBtn_TakeAway.setVisibility(VISIBLE);

                vPayBtn_EatHere.setVisibility(GONE);
                vPayBtn_Delivery.setVisibility(GONE);
            }


            if(i==R.id.pay_options_Delivery)
            {
                type_short="DL";
                __FinalCost=Float.toString(getInitialPrice(true));
                UpdateItemsView(itemsList,lItemsHolderVerti,false,3);
                UpdateDeliveryCost(vHostel.getSelectedItem().toString(),true);


                findViewById(R.id.pay_Address).setVisibility(View.VISIBLE);
                findViewById(R.id.pay_Address).setAlpha(0f);

                findViewById(R.id.pay_Address)
                        .animate()
                        // .translationY(findViewById(R.id.pay_Address).getHeight())
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                findViewById(R.id.pay_Address).setVisibility(View.VISIBLE);
                            }
                        });


                OrderType=3;

                if(mess.equals("A")) {
                    if (!messADelivery.equals("opened")) {
                        vPayBtn_Delivery.setVisibility(GONE);
                        Delivery.setOnClickListener(serviceNotAvailable);
                    } else {
                        vPayBtn_Delivery.setVisibility(VISIBLE);
                        Delivery.setOnClickListener(null);
                    }
                }else
                if(mess.equals("C")) {
                    if (!messCDelivery.equals("opened")) {
                        vPayBtn_Delivery.setVisibility(GONE);
                        Delivery.setOnClickListener(serviceNotAvailable);
                    } else {
                        vPayBtn_Delivery.setVisibility(VISIBLE);
                        Delivery.setOnClickListener(null);
                    }
                }
                else
                if(mess.equals("D")) {
                    if (!messDDelivery.equals("opened")) {
                        vPayBtn_Delivery.setVisibility(GONE);
                            Delivery.setOnClickListener(serviceNotAvailable);
                    } else {
                        vPayBtn_Delivery.setVisibility(VISIBLE);
                        Delivery.setOnClickListener(null);
                    }
                }
                else
                if(mess.equals("INS")) {

                    del_type = 1;

                    if (!INSDelivery.equals("opened")) {
                        Delivery.setOnClickListener(serviceNotAvailable);
                        vPayBtn_Delivery.setVisibility(GONE);
                    } else {

                        if(getInitialPrice(false)>= 100.0) {

                            Delivery.setEnabled(true);
                            vPayBtn_Delivery.setVisibility(VISIBLE);
                        }

                        else
                        {
                            Delivery.setOnClickListener(deliveryNotAvailable);
                            vPayBtn_Delivery.setVisibility(GONE);
                        }
                    }


                  /*  if (!INSDelivery.equals("opened")) {
                        vPayBtn_Delivery.setVisibility(GONE);
                        Delivery.setOnClickListener(serviceNotAvailable);
                    } else {
                        vPayBtn_Delivery.setVisibility(VISIBLE);
                        Delivery.setOnClickListener(null);
                    }*/
                }

                vPayBtn_EatHere.setVisibility(GONE);
                vPayBtn_TakeAway.setVisibility(GONE);

            }


        }
    };



    //Payment gateway

    public void LaunchUpiChooser()
    {

        Toast.makeText(this, "After payment please wait for receipt page\n"+
                "", Toast.LENGTH_LONG).show();

        TextView tv_totalcost = v_TOTALCOST.findViewById(R.id.template_bill_price);
        Intent intent;
        Thread thread = new Thread();
        thread.setDaemon(true);
        thread.start();

        //Create url for payment
        StringBuilder stringBuilder = new StringBuilder("upi://pay?pa=");
        stringBuilder.append(PAY_ID);
        stringBuilder.append("&pn="+mess+" "+type_short+"&tr=");
        stringBuilder.append("465");
        stringBuilder.append("A&tn="+mess+" "+type_short+"&am=");
        stringBuilder.append((String) tv_totalcost.getText().toString());
        //stringBuilder.append("1");
        stringBuilder.append("&cu=INR");


        Uri parse = Uri.parse(stringBuilder.toString());
        List arrayList = new ArrayList();

        if(isPackageInstalled(PaymentPage.this,"com.google.android.apps.nbu.paisa.user")) {
            intent = new Intent("android.intent.action.VIEW", parse);
            intent.setPackage("com.google.android.apps.nbu.paisa.user");
            arrayList.add(intent);
        }

  /*      if (isPackageInstalled(PaymentPage.this,"net.one97.paytm")) {
            intent = new Intent("android.intent.action.VIEW", parse);
            intent.setPackage("net.one97.paytm");
            arrayList.add(intent);
        }*/

        if(arrayList.isEmpty())
        {
            Toast.makeText(this, "We are only accepting Google Pay for payments", Toast.LENGTH_SHORT).show();
            vPayBtn_EatHere.clearAnimation();
            vPayBtn_TakeAway.clearAnimation();
            vPayBtn_Delivery.clearAnimation();
        }
        else {

            intent = Intent.createChooser((Intent) arrayList.remove(0), "Choose your UPI Payment App");
            intent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));

            startActivityForResult(intent, 1, null);
        }
    }

    public boolean isPackageInstalled(Context context, String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

        if(intent == null)
        {
            return false;
        }

        return  true;
    }


    private boolean isAPIAvailable(String api_pkg_name)
    {


        try
        {
            getPackageManager().getPackageInfo(api_pkg_name,PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException unused)
        {
            Log.d("schwifty2","api not available");
        }
        return false;
    }



    //Payment Listners

    String rnd;
    DatabaseReference trackOrderUID;
    View.OnClickListener payDelivery=new View.OnClickListener() {
    @Override
    public void onClick(View view) {

            //save order
            _hostel = vHostel.getSelectedItem().toString();
            _room = vRoom.getText().toString();
            _name = vName.getText().toString();
            _mobile = vMob.getText().toString();

            if (TextUtils.isEmpty(_name)) {
                Toast.makeText(PaymentPage.this, "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(_mobile)) {
                Toast.makeText(PaymentPage.this, "Enter your phone no.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(_hostel)) {
                Toast.makeText(PaymentPage.this, "Select a hostel", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(_room)) {
                Toast.makeText(PaymentPage.this, "Enter your address", Toast.LENGTH_SHORT).show();
            } else {
                Animation anim = AnimationUtils.loadAnimation(PaymentPage.this, R.anim.rotate);

                vPayBtn_Delivery.startAnimation(anim);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                rnd = formatter.format(c.getTime());

                TextView tv_tc = v_TOTALCOST.findViewById(R.id.template_bill_price);


                DatabaseReference trackOrderUID = trackRef.child(_hostel + "_" + _room + "=" + rnd);

                //not working as of now
                //  writeToFile("OrderId = "+UID,"Delivery_Order.txt",PaymentPage.this);

                String initialCost = Float.toString( getInitialPrice(true));

                trackOrderUID.child("Name").setValue(_name);
                trackOrderUID.child("isPaid").setValue("false");
                trackOrderUID.child("Mob").setValue(_mobile);
                trackOrderUID.child("Cost").setValue(tv_tc.getText().toString());
                trackOrderUID.child("mode").setValue("Delivery");
                trackOrderUID.child("DeliveryCost").setValue(deliveryCost);
                trackOrderUID.child("status").setValue("new");
                trackOrderUID.child("token").setValue("0");
                trackOrderUID.child("device_token_id").setValue(tokenId);
                trackOrderUID.child("Mess").setValue(mess);
                trackOrderUID.child("deviceId").setValue(android_id);
                trackOrderUID.child("OrderUID").setValue(UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LaunchUpiChooser();
                    }
                });
            }
        }
    };



    View.OnClickListener payEatHere  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //save order
            _hostel = vHostel.getSelectedItem().toString();
            _room = vRoom.getText().toString();
            _name = vName.getText().toString();
            _mobile = vMob.getText().toString();

            if(mess.equals("RC")) {
                //To pre check the no of tables available
                //First retrive the no of Available tables from dastabase
                //add listner to edittext and search if there are available tables
                //if not turn off the pay button
                vNPeople = findViewById(R.id.pay_NPeople_text);
                _NPeople = vNPeople.getText().toString();
            }
            else
            {
                _NPeople = "n";
            }

            if (TextUtils.isEmpty(_name)) {
                Toast.makeText(PaymentPage.this, "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(_mobile)) {
                Toast.makeText(PaymentPage.this, "Enter your phone no.", Toast.LENGTH_SHORT).show();
            }
            else
            if(TextUtils.isEmpty(_NPeople))
            {
                Toast.makeText(PaymentPage.this, "Please enter the no. of people", Toast.LENGTH_SHORT).show();
            }

            else {
                Animation anim = AnimationUtils.loadAnimation(PaymentPage.this, R.anim.rotate);

                vPayBtn_EatHere.startAnimation(anim);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyhhmmss");
               // rnd = formatter.format(c.getTime());

                DatabaseReference trackRef = FirebaseDatabase.getInstance().getReference().child("TrackNonDeliveryOrders");
                trackOrderUID = trackRef.push();

                //not working as of now
                //writeToFile("OrderId = "+UID,"Delivery_Order.txt",PaymentPage.this);

                if(mess.equals("RC"))
                {
                    trackOrderUID.child("nPeople").setValue(_NPeople);
                }

                trackOrderUID.child("Name").setValue(_name);
                trackOrderUID.child("isPaid").setValue("false");
                trackOrderUID.child("Mob").setValue(_mobile);
                trackOrderUID.child("Cost").setValue(__FinalCost);
                trackOrderUID.child("mode").setValue("EatHere");
                trackOrderUID.child("status").setValue("new");
                trackOrderUID.child("token").setValue(type_short + token);
                trackOrderUID.child("device_token_id").setValue(tokenId);
                trackOrderUID.child("Mess").setValue(mess);
                trackOrderUID.child("deviceId").setValue(android_id);
                trackOrderUID.child("OrderUID").setValue(UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LaunchUpiChooser();
                    }
                });
            }
        }

    };



    View.OnClickListener payTakeAway = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //save order
            _hostel = vHostel.getSelectedItem().toString();
            _room = vRoom.getText().toString();
            _name = vName.getText().toString();
            _mobile = vMob.getText().toString();

            if (TextUtils.isEmpty(_name)) {
                Toast.makeText(PaymentPage.this, "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(_mobile)) {
                Toast.makeText(PaymentPage.this, "Enter your phone no.", Toast.LENGTH_SHORT).show();
            } else {
                Animation anim = AnimationUtils.loadAnimation(PaymentPage.this, R.anim.rotate);

                vPayBtn_TakeAway.startAnimation(anim);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyhhmmss");
               // rnd = formatter.format(c.getTime());

                DatabaseReference trackRef = FirebaseDatabase.getInstance().getReference().child("TrackNonDeliveryOrders");
                trackOrderUID = trackRef.push();

                //not working as of now
                //writeToFile("OrderId = "+UID,"Delivery_Order.txt",PaymentPage.this);


                trackOrderUID.child("Name").setValue(_name);
                trackOrderUID.child("isPaid").setValue("false");
                trackOrderUID.child("Mob").setValue(_mobile);
                trackOrderUID.child("Cost").setValue(__FinalCost);
                trackOrderUID.child("mode").setValue("TakeAway");
                trackOrderUID.child("status").setValue("new");
                trackOrderUID.child("token").setValue(type_short + token);
                trackOrderUID.child("device_token_id").setValue(tokenId);
                trackOrderUID.child("Mess").setValue(mess);
                trackOrderUID.child("deviceId").setValue(android_id);
                trackOrderUID.child("OrderUID").setValue(UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LaunchUpiChooser();
                    }
                });
            }
        }


    };

    View.OnClickListener serviceNotAvailable = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(PaymentPage.this, "This service is not available at the moment", Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener deliveryNotAvailable = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(PaymentPage.this, "Delivery is not available for purchases below \u20B9 100", Toast.LENGTH_SHORT).show();
        }
    };

    class Items
    {
        private String name,qty,price,pkgPrice;

        public Items(String name, String qty, String price, String pkgPrice) {
            this.name = name;
            this.qty = qty;
            this.price = price;
            this.pkgPrice = pkgPrice;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPkgPrice() {
            return pkgPrice;
        }

        public void setPkgPrice(String pkgPrice) {
            this.pkgPrice = pkgPrice;
        }
    }


}
