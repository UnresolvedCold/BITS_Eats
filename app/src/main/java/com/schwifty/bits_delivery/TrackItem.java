package com.schwifty.bits_delivery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackItem extends AppCompatActivity {

    private Toolbar mToolbar;

    private ItemAdapter adapter;

    private TextView vRoom;
    private Spinner vHostel;
    private View vGo;

    private RecyclerView vTrackOrderList;

    private String HostelRoom;

    private DatabaseReference trackRef;
    private DatabaseReference orderRef;

    private List<String> orderUIDList;

    private  Context cntx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_item);

        cntx=TrackItem.this;

        mToolbar=findViewById(R.id.track_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  BITS Food Delivery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable dr = getResources().getDrawable(R.drawable.logo);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 75, 75, true));

        getSupportActionBar().setIcon(d);

        orderUIDList = new ArrayList<>();

        vRoom = findViewById(R.id.track_Room);
        vHostel = findViewById(R.id.track_hostel);
        vGo = findViewById(R.id.track_update);
        vTrackOrderList=findViewById(R.id.track_orderList);

        trackRef = FirebaseDatabase.getInstance().getReference().child("TrackOrder");
        orderRef =FirebaseDatabase.getInstance().getReference().child("Order");

        vGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Updates
                HostelRoom=vHostel.getSelectedItem().toString().trim()+vRoom.getText().toString().trim();

                trackRef.child(HostelRoom).child("OrderList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        int i =1;
                        while(dataSnapshot.hasChild("Order_"+i))
                        {
                            String eUID=dataSnapshot.child("Order_"+i).child("UID").getValue().toString();

                            orderUIDList.add(eUID);

                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Update();

            }

            private void Update()
            {
                vTrackOrderList.setLayoutManager(new LinearLayoutManager(cntx));

                adapter = new ItemAdapter(cntx,orderUIDList);

                vTrackOrderList.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    public class Items
    {
        private String UID;
        private String Date;

        public Items(){}

        public Items(String UID, String date) {
            this.UID = UID;
            Date = date;
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }
    }



    public static class ItemHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView vUID;
        TextView vMess;
        TextView vAdress;
        TextView vOTP;

        RecyclerView vItemList;

        String uid,mess,adress,otp;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            vUID = mView.findViewById(R.id.template_track_UID);
            vMess=mView.findViewById(R.id.template_track_Mess);
            vAdress = mView.findViewById(R.id.template_track_Address);
            vItemList = mView.findViewById(R.id.template_track_Items);

        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
            vUID.setText(uid);
        }

        public String getMess() {
            return mess;
        }

        public void setMess(String mess) {
            this.mess = mess;
            vMess.setText(mess);
        }

        public String getAdress() {
            return adress;
        }

        public void setAdress(String adress) {
            this.adress = adress;
            vAdress.setText(adress);
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
            vOTP.setText(otp);
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemHolder>
    {
        private Context mCtx;

        private List<String> mList;

        public ItemAdapter(Context mCtx, List<String> mList) {
            this.mCtx = mCtx;
            this.mList = mList;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            LayoutInflater inflater = LayoutInflater.from(mCtx);

            View view = inflater.inflate(R.layout.template_track_items,null);

            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

            final String item = mList.get(i);
            //SeT View
            itemHolder.setUid(item);

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
