package com.schwifty.bits_delivery;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private PlaceOrderActivity parentActivity;

    private DatabaseReference menuReference;

    private FirebaseRecyclerAdapter<Items, ItemHolder> firebaseRecyclerAdapter;

    private RecyclerView vListItems;

    private SelectedItemsList list;

    private EditText searchText;
    private View searchBtn;
    private  View menuSearch;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView= inflater.inflate(R.layout.fragment_menu, container, false);

        Log.d("hundred","Entered Menu Fragment On Create ");

        parentActivity = (PlaceOrderActivity) getActivity();

        String option = parentActivity.MessOption;

        vListItems = mainView.findViewById(R.id.menu_listItems);

        searchBtn=mainView.findViewById(R.id.menu_SearchButton);
        searchText=mainView.findViewById(R.id.menu_SearchItem);
        menuSearch = mainView.findViewById(R.id.menu_Search);


        Calendar calendar = Calendar.getInstance();
        //String day = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));

        String day = "0";

        menuReference = FirebaseDatabase.getInstance().getReference()
                .child("Menu").child("Mess").child(option).child(day);
        menuReference.keepSynced(true);

       // list = new SelectedItemsList();
        list = parentActivity.selListTest;


        menuReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RetriveMenuItems("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                RetriveMenuItems(searchText.getText().toString());
            }
        });

       // menuSearch.setVisibility(View.GONE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetriveMenuItems(searchText.getText().toString());
            }
        });

        vListItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {

                }

                if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {

                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        return mainView;
    }

    private void RetriveMenuItems(final String value)
    {
        Query query = menuReference;
        if(!TextUtils.isEmpty(value))
        {
            query=menuReference;//.orderByChild("Name").startAt(value).endAt(value+"\uf8ff");
        }
        else
        {
             query=menuReference;//.orderByChild("Name").startAt(" "+"\uf8ff").endAt(" "+"\uf8ff");
        }

        FirebaseRecyclerOptions Adapter_Options = new FirebaseRecyclerOptions.Builder<Items>().setQuery(
               query,
                new SnapshotParser<Items>() {
                    @NonNull
                    @Override
                    public Items parseSnapshot(@NonNull DataSnapshot snapshot) {

                        Log.d("hundred","Entered Adapter Options ");

                        if(snapshot.hasChild("Name"))
                        {

                            Log.d("hundred","Entered Adapter's if ");

                            String name = snapshot.child("Name").getValue().toString();
                            String price = snapshot.child("Price").getValue().toString();
                            String isAvailable = snapshot.child("isAvailable").getValue().toString();
                            String isPackagable = snapshot.child("isPackagable").getValue().toString();
                            String UID = snapshot.getKey().toString();
                            String packingPrice = snapshot.child("packingPrice").getValue().toString();

                            return new Items(name,price,isPackagable,isAvailable,UID,packingPrice);
                        }
                        else
                        {
                            Log.d("hundred","Entered Adapter's else ");

                            return new Items("loading","loading","loading","loading","loading","loading");
                        }

                    }
                }
        ).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, ItemHolder>(Adapter_Options) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemHolder holder, int position, @NonNull final Items model) {

                Log.d("hundred","Entered on Bind View Holder ");

                holder.setAvailability(model.getIsAvailable());
                holder.ShowSearch(model.getName(),value);
                holder.setName(model.getName());
                holder.setPrice(model.getPrice());
                holder.setUID(model.getUID());

                holder.vAddToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        SelectedItems currItem = new SelectedItems
                                (
                                        model.getUID(),
                                        model.getName(),
                                        Float.parseFloat(model.getPrice()),
                                        1,
                                        model.getIsPackagable(),
                                        model.getPackingPrice()
                                );
                        Log.d("hundred_2",model.getIsPackagable()+"");

                        //get spinner count
                        AddToCart(currItem);

                        // list.GenerateLog();

                        UpdatePaymentFragment();
                        //Log.d("hundred","Exit Adapter ");

                        //show animation
                        holder.showAnimation(getContext());




                        //Cart Shaking Animation
                        Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.shake);
                        getActivity().findViewById(R.id.menu_cart).startAnimation(anim);

                    }
                });

            }

            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                Log.d("hundred","Entered Create View Holder ");

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_menu_items,viewGroup,false);
                return new ItemHolder(view);
            }
        };


        vListItems.setLayoutManager(new LinearLayoutManager(getContext()));

        vListItems.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    private void UpdatePaymentFragment() {


    }

    private void AddToCart(SelectedItems itm) {
        list.AddToList(itm);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null)
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    //Start of other term
    public MenuFragment(){}

    public class Items
    {
        private String Name,Price,isPackagable,isAvailable,UID,packingPrice;

        Items(){}

        public Items(String name, String price,String isPackagable ,String isAvailable,String UID,String packingPrice) {
            Name = name;
            Price = price;
            this.isPackagable=isPackagable;
            this.isAvailable = isAvailable;
            this.UID=UID;
            this.packingPrice=packingPrice;
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

        public Boolean getIsPackagable()
        {
            return Boolean.parseBoolean(isPackagable);
        }

        public float getPackingPrice()
        {
            return Float.parseFloat(packingPrice);
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
        View vAddToCartBtn;
        Spinner vCount;

        String UID;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            vName=mView.findViewById(R.id.templateMenu_ItemName);
            vPrice=mView.findViewById(R.id.templateMenu_ItemPrice);
            vAddToCartBtn=mView.findViewById(R.id.templateMenu_AddButton);
            //vCount = mView.findViewById(R.id.templateMenu_nItems);
        }

        public void setName(String name)
        {
            vName.setText(name);
        }

        public void setPrice(String price)
        {
            vPrice.setText("( Rs. "+price+" )");
        }

        public void setUID(String uid)
        {
            UID=uid;
        }

        public void showAnimation(Context cntx)
        {
            Animation anim = AnimationUtils.loadAnimation(cntx,R.anim.fadein);
            mView.startAnimation(anim);
        }

        public void ShowSearch(String itemName,String searchItem)
        {
            String _itemName = itemName.toLowerCase();
            if(TextUtils.isEmpty(searchItem))
            {

            }
            else if(_itemName.contains(searchItem.toLowerCase()))
            {

            }
            else
            {
               setAvailability("false");
            }
        }

        public void setAvailability(String isAvailable)
        {

            if(isAvailable.equals("false"))
            {
                mView.setEnabled(false);
                mView.setVisibility(GONE);
                mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

            }

        }
    }
}

/*
    Item Data =
        Name
        Price
        isInStock
 */


