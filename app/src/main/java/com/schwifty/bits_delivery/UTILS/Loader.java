package com.schwifty.bits_delivery.UTILS;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.schwifty.bits_delivery.ProfileActivity;
import com.schwifty.bits_delivery.R;

public class Loader
{

    private String title ="Enter the passcode";
    private int content = R.layout.dialog_confirm_mess_skip;
    private Activity activity;
    private boolean onTouchOutside=true;

    Loader(){}

    public Loader(String title, int content, Activity activity, boolean onTouchOutside) {
        this.title = title;
        this.content = content;
        this.activity = activity;
        this.onTouchOutside = onTouchOutside;
    }

    public Dialog CreateLoader()
    {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(content);
        dialog.setTitle(title);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(onTouchOutside);

        return dialog;
    }

}
