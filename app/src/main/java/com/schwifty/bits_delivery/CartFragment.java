package com.schwifty.bits_delivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private PlaceOrderActivity parentActivity;

    private float threshold;

    private ItemAdapter adapter;

    private SelectedItemsList list;

    private TextView vTotalCost;
    private RecyclerView vSelectedItems;
    private View vProceed;
    private View vHelp;

    private EditText eRoom;
    private Spinner eHostel;

    private float packagingPrice;
    private float deliveryCost;
    DatabaseReference variablesRef;

    private String mess;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        parentActivity = (PlaceOrderActivity)getActivity();

        list = parentActivity.selListTest;
        mess = parentActivity.MessOption;

        final String MessOption = parentActivity.MessOption;

        vTotalCost=view.findViewById(R.id.cart_TotalCost);
        vSelectedItems=view.findViewById(R.id.cart_selectedItems);
        vProceed = view.findViewById(R.id.cart_proceed_float);
        vHelp=view.findViewById(R.id.cart_help);

        eHostel = view.findViewById(R.id.cart_Hostel);
        eRoom = view.findViewById(R.id.cart_RoomNo);

        vProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float cost = list.GetTotalPrice();
                if(cost<5.0f)
                {
                    Toast.makeText(getContext(), "Select some items first", Toast.LENGTH_SHORT).show();
                }
                else {

                    //Save order to Database
                    //Order / UID / <Mess> / Item - numbers - address - isPricePaid

                    Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);

                    vProceed.startAnimation(anim);

                    DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference()
                            .child("Order");


                    final String OrderUID = orderReference.push().getKey();

                    orderReference.child(OrderUID).child("Mess").setValue(MessOption);

                    list.ProceedToPayment(orderReference.child(OrderUID),getContext(),mess,list);

                }
            }
        });

        vHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AleartDialog);
                builder.setTitle("Total Cost Calculations");
               // final EditText groupNameField = new EditText(getContext());
                // groupNameField.setHint("Enter the name of group");

                TextView i,d,p,t;
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_view,null,false);

                i=dialogView.findViewById(R.id.dialog_initialPrice);
                d=dialogView.findViewById(R.id.dialog_delivery);
                p=dialogView.findViewById(R.id.dialog_packaging);
                t=dialogView.findViewById(R.id.dialog_total);

                i.setText("Initial Price : Rs."+Float.toString(list.GetTotalPrice()));

                if(list.GetTotalPrice()>threshold) {
                    d.setText("Delivery Cost : Rs." + Float.toString(deliveryCost));
                    p.setText("Packaging Cost : Rs." + Float.toString(packagingPrice));
                    t.setText("Total Price : Rs." + Float.toString(list.GetTotalPrice() + deliveryCost + packagingPrice));
                }
                else
                {
                    d.setText("Delivery Cost : Rs." + 0.0f);
                    p.setText("Packaging Cost : Rs." + 0.0f);
                    t.setText("Total Price : Rs." + 0.0f);
                }

                builder.setView(dialogView);

                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
/*
        variablesRef = FirebaseDatabase.getInstance().getReference().child("Variables");
        variablesRef.keepSynced(true);

        variablesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("DeliveryCost"))
                {
                    deliveryCost = getDeliveryCost(dataSnapshot.child("DeliveryCost").getValue().toString());
                }
                else
                {
                    deliveryCost = 10.0f;
                }

                if(dataSnapshot.hasChild("PackagingCost"))
                {
                    if(list.IsPackagable()) {
                        packagingPrice = getPackagingPrice(dataSnapshot.child("PackagingCost").getValue().toString());
                        SelectedItemsList.packagingPrice = packagingPrice;
                    }
                    else
                    {
                        packagingPrice=0.0f;
                    }
                }
                else
                {
                    if(list.IsPackagable())
                    {
                        packagingPrice=5.0f;
                    }
                    else
                    {
                        packagingPrice=0.0f;
                    }

                }

                threshold = packagingPrice+deliveryCost;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        vProceed.clearAnimation();

    }

    public void Update()
    {
        list.GenerateLog();

        float totalCost = list.GetTotalPrice();
        if(totalCost<=threshold)
        {
            vTotalCost.setText("Initial Cost : Rs. " + 0.0f);
        }
        else {
            vTotalCost.setText("Initial Cost : Rs. " + Float.toString(totalCost + packagingPrice + deliveryCost)+" (inc. pkg)");
        }

        vSelectedItems.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ItemAdapter(getContext(),list);

        vSelectedItems.setAdapter(adapter);
    }

    public float getPackagingPrice(String value)
    {
        return Float.parseFloat(value);
    }

    public float getDeliveryCost(String value)
    {
        return Float.parseFloat(value);
    }

    public class Items
    {
        private String Name,Price,isAvailable,UID;

        Items(){}

        public Items(String name, String price, String isAvailable,String UID) {
            Name = name;
            Price = price;
            this.isAvailable = isAvailable;
            this.UID=UID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getPrice() {
            return Price;
        }

        public String getUID()
        {
            return UID;
        }

        public void setPrice(String price) {
            Price = price;
        }

        public String getIsAvailable() {
            return isAvailable;
        }

        public void setIsAvailable(String isAvailable) {
            this.isAvailable = isAvailable;
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView vName;
        TextView vPrice;
        View vRemoveFromCartBtn;
        TextView vCount;

        String UID;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            vName=mView.findViewById(R.id.templateSelected_ItemName);
            vPrice=mView.findViewById(R.id.templateSelected_ItemPrice);
            vRemoveFromCartBtn=mView.findViewById(R.id.templateSelected_SubtractButton);
            vCount = mView.findViewById(R.id.templateSelected_nItems);
        }

        public void setName(String name)
        {
            vName.setText(name);
        }

        public void setPrice(String price)
        {
            vPrice.setText("( Rs. "+price+" )");
        }

        public void setNItems(String nItems)
        {
            vCount.setText(nItems);
        }

        public void setUID(String uid)
        {
            UID=uid;
        }

        public void showAnimation(Context cntx)
        {
            Animation anim = AnimationUtils.loadAnimation(cntx,R.anim.bounce);

            mView.startAnimation(anim);
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemHolder>
    {
        private Context mCtx;

        private SelectedItemsList mList;

        public ItemAdapter(Context mCtx, SelectedItemsList mList) {
            this.mCtx = mCtx;
            this.mList = mList;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            LayoutInflater inflater = LayoutInflater.from(mCtx);

            View view = inflater.inflate(R.layout.template_selected_items,null);

            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemHolder itemHolder, int i) {

           final SelectedItems item = mList.get(i);

           itemHolder.setName(item.getName());
           itemHolder.setPrice(Float.toString(mList.GetPrice(item)));
           itemHolder.setNItems(Integer.toString(mList.getCount(item)));

           itemHolder.vRemoveFromCartBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    list.RemoveFromList(item);
                    Update();
               }
           });

        }

        @Override
        public int getItemCount() {
            return mList.getCount();
        }
    }

}
