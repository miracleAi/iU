package com.android.biubiu.component.util;

public class BitmapUtils {
	public static BitmapUtils bitUtils ;
	public static BitmapUtils getInstance(){
		if(bitUtils == null){
			bitUtils = new BitmapUtils();
		}
		return bitUtils;
	}
	
}
