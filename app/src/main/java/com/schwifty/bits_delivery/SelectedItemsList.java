package com.schwifty.bits_delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.schwifty.bits_delivery.UTILS.TableList;

import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectedItemsList
{
    List<SelectedItems> list;
    public static float packagingPrice = 0.0f;
    public static float deliveryCost = 0.0f;

    private String intent_infoAboutItems="";

    public SelectedItemsList() {
        list=new ArrayList<>();
    }

    public SelectedItemsList(List<SelectedItems> list) {
        this.list = list;
    }

    public void AddToList(SelectedItems itm)
    {
        if(isItemPresent(itm))
        {
            ChangeCount(itm,1);
        }
        else {
            itm.setnItems(1);
            list.add(itm);
        }
    }

    public void RemoveFromList(SelectedItems itm)
    {
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(itm.isEqual(iItm))
            {
                if(getCount(itm)>1) {
                    ChangeCount(iItm, -1);
                }
                else
                {
                    list.remove(iItm);
                }
                break;
            }
        }
    }

    public void RemoveAllOccurance(SelectedItems itm)
    {
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(itm.isEqual(iItm))
            {
                list.remove(iItm);
            }
        }
    }

    public int getCount()
    {
        return list.size();
    }

    public int getCount(SelectedItems itm)
    {
        int count=0;
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(itm.isEqual(iItm))
            {
                count+=iItm.getnItems();
            }
        }
        return count;
    }

    public Boolean isItemPresent(SelectedItems itm)
    {
        if(getCount(itm)>0)
        {
            return true;
        }
        return false;
    }

    public void ChangeCount(SelectedItems itm,int dn)
    {
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(itm.isEqual(iItm))
            {
                iItm.setnItems(iItm.getnItems()+dn);
            }
        }
    }

    public void GenerateLog()
    {
        Log.d("hundred",""+getCount());

        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();

            Log.d("hundred","Name = "+iItm.getName()+" Price = "+iItm.getPrice()
                    +" Count = "+iItm.getnItems());

        }

    }

    public float GetTotalPrice()
    {
        if(list.size()==0)
        {
            return 0;
        }

        float sum =0.0f;

        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();

            sum+=(iItm.getnItems()*iItm.getPrice());
            sum+=iItm.getnItems()*GetPackingPrice(iItm);

        }

        return sum+deliveryCost;
    }

    public void SaveListToDatabase(DatabaseReference rootOrder)
    {
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();

            rootOrder.child("Ordered Items").child(iItm.getName()).setValue(iItm.getnItems());
            rootOrder.child("isPaid").setValue("false");

        }

    }

    int iCount=0;
    public void ProceedToPayment(DatabaseReference rootOrder, final Context cntx, final String mess, final SelectedItemsList list)
    {

        final String orderUID = rootOrder.getKey().toString();


        final int size = list.getCount();

        final Iterator<SelectedItems> i =this.list.iterator();
        intent_infoAboutItems="";

        final Intent intent = new Intent(cntx, PaymentPage.class);


        while(i.hasNext())
        {
            final SelectedItems iItm = i.next();

            rootOrder.child("Ordered Items").child(iItm.getName()).setValue(iItm.getnItems());
            rootOrder.child("isPaid").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                String f_Name,f_nItems,f_price,f_pkgprice,f_items;
                f_Name = iItm.getName();
/*
                f_Name = iItm.getName();
                f_items= String.format(("%-10s %10s %10s %10s")
                        ,f_Name,getCount(iItm),
                        GetPrice(iItm),iItm.getPackingPrice());
*/

                f_Name = iItm.getName()+"_";
                f_nItems = getCount(iItm)+"_" ;
                f_price=GetPrice(iItm)+"_" ;
                f_pkgprice= (iItm.getPackingPrice()*getCount(iItm))+"=";

                f_items = f_Name+f_nItems+f_price+f_pkgprice;

                intent_infoAboutItems += f_items;

                iCount++;
                if(iCount >= size) {

                        Log.d("hundred","f_items : "+ f_items);

                        intent.putExtra("items", intent_infoAboutItems);
                        intent.putExtra("TotalCost", "" + (list.GetTotalPrice()));
                        intent.putExtra("isPackagable", list.IsPackagable());
                        intent.putExtra("UID", orderUID);
                        intent.putExtra("Mess",mess);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        cntx.startActivity(intent);
                        ((Activity)cntx).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
            });
        }
    }

    public float GetPrice(SelectedItems itm)
    {
        Float cost = 0.0f;
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(iItm.isEqual(itm))
            {
                cost+=iItm.getPrice()*iItm.getnItems();
            }
        }
        return cost;

    }

    public Boolean IsPackagable()
    {
        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            if(IsPackagable(i.next()))
            {
                return true;
            }
        }

        return false;
    }

    public Boolean IsPackagable(SelectedItems itm)
    {

        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(iItm.isEqual(itm))
            {
                return itm.IsPackagable();
            }
        }

        return false;
    }

    public float GetPackingPrice(SelectedItems itm)
    {

        Iterator<SelectedItems> i =list.iterator();

        while(i.hasNext())
        {
            SelectedItems iItm = i.next();
            if(iItm.isEqual(itm))
            {
                return itm.getPackingPrice();
            }
        }

        return 0.0f;
    }

    public SelectedItems get(int pos)
    {
        return list.get(pos);
    }

}