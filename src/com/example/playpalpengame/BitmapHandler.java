package com.example.playpalpengame;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHandler {
	public static ArrayList<Bitmap> bitmapArr = new ArrayList<Bitmap>();
	
	public static Bitmap getLocalBitmap(Context con, int resourceId){
	    InputStream inputStream = con.getResources().openRawResource(resourceId);
	    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, getBitmapOptions());
	    bitmapArr.add(bitmap);
	    return bitmap;
	}
	
	public static BitmapFactory.Options getBitmapOptions(){
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    return options;
	}
	
	public static void recycleBitmaps() {
		for(int i=0; i<bitmapArr.size(); i++) {
			if(!bitmapArr.get(i).isRecycled()) 
				bitmapArr.get(i).recycle();
		}
		bitmapArr.clear();
	}
}
