package com.schwifty.bits_delivery.UTILS;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PrintPdf {

    public PrintPdf()
    {
      //  LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
       // RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.activity_main, null); //RelativeLayout is root view of my UI(xml) file.
       // root.setDrawingCacheEnabled(true);
      //  Bitmap screen= getBitmapFromView(this.getWindow().findViewById(R.id.relativelayout)); // here give id of our root layout (here its my RelativeLayout's id)

    }

    public void takeScreenShot(View ScrollView,Context mContext)
    {
        View u = ScrollView;

        HorizontalScrollView z = (HorizontalScrollView) ScrollView;
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(u,totalHeight,totalWidth);

        //Save bitmap
        String extr = Environment.getExternalStorageDirectory()+"/Folder/";
        String fileName = "report.jpg";
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b, "Screen", "screen");
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

}
