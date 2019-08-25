package com.schwifty.bits_delivery.UTILS;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.schwifty.bits_delivery.ProfileActivity;
import com.schwifty.bits_delivery.R;

public class Loader
{

    private Dialog dialog;

    public Loader(String title, int content, Activity activity, boolean cancelOnTouchOutside) {
        dialog = new Dialog(activity);
        dialog.setContentView(content);
        dialog.setTitle(title);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
    }

    public Dialog getDialog()
    {
        return dialog;
    }



}
