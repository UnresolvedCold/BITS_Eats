package com.schwifty.bits_delivery.UTILS;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TableList {

    private static String f_str="";

    public static String AlignedCenter(String str,int maxLength,char fil)
    {
        f_str=null;
        if(maxLength < str.length())
        {
            return str ;
        }

        int pad_len = (maxLength - str.length())/2;

        for(int i = 0; i < pad_len ; i++)
        {
            f_str+=fil;
        }

        f_str+=str;

        for (int i = 0; i < pad_len; i++)
        {
            f_str+=fil;
        }

        Log.d("holohola",f_str);

        return  f_str;
    }

    public static String AlignLeft(String str,int maxLength,char fill)
    {
        f_str="";
        if(str.length()>maxLength)
        {
            return str;
        }

        int nFills = maxLength-(str.trim()).length();

        f_str+=str.trim();

        for (int i = 0 ; i < nFills;i++)
        {
            f_str+=fill;
        }
        Log.d("TableList",f_str);
        return  f_str;
    }

    public static String AlignRight(String str,int maxLength,char fill)
    {
        f_str="";
        if(str.length()>maxLength)
        {
            return str;
        }

        int nFills = maxLength-str.length();

        for (int i = 0 ; i < nFills;i++)
        {
            f_str+=fill;
        }
        f_str+=str;

        return  f_str;
    }

}
