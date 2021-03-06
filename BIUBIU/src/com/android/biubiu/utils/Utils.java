package com.android.biubiu.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utils {
	public static String getDeviceID(Context context) {
		String deviceID = null;
		if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
			//            String deviceId = "";
			if (checkPhoneState(context)) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = ((tm == null) ? "" : tm.getDeviceId());
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				String mac_address = ((wifiManager == null) ? ""
						: wifiManager.getConnectionInfo()
						.getMacAddress());

				if((imei == null || imei.equals("")) && (mac_address == null || mac_address.equals(""))){
					//序列号（sn）
					String ssn = tm==null ? "sn:" : tm.getSimSerialNumber();
					ssn = ssn==null ? "sn:" : "sn:"+ssn;

					//如果上面都没有， 则生成一个id：随机码
					String uuid = getUUID(context);
					uuid = uuid==null ? "uuid:" : "uuid:"+uuid;
					//序列号不存在的时候才会使用随机ID
					ssn = ssn.equals("sn:") ? uuid : ssn;
					deviceID = getMD5(ssn);
				} else{

					deviceID = getMD5(setToNoNull(imei) + "-" + setToNoNull(mac_address));
				}
			}          
		} 
		return deviceID;
	}
	private static String getString(byte[] b) {
		StringBuffer buf = new StringBuffer("");
		int i = 0;

		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];

			if (i < 0) {
				i += 256;
			}

			if (i < 16) {
				buf.append("0");
			}

			buf.append(Integer.toHexString(i));
		}

		return buf.toString();
	}
	public static String getMD5(String val) {
		MessageDigest md5 = null;

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e(null, e.getMessage(), e);
		}

		md5.update(val.getBytes());

		byte[] m = md5.digest(); // 加密

		return getString(m);
	}
	/**
	 * 得到全局唯一UUID
	 */
	public static String getUUID(Context context){

		SharedPreferences mShare = PreferenceManager.getDefaultSharedPreferences(context);
		String uuid = null;
		if(mShare != null){
			uuid = mShare.getString("biubiu_uuid", "");
		}

		if(uuid != null && !uuid.equals("")){
			uuid = UUID.randomUUID().toString();
			Editor editor = mShare.edit();
			editor.putString("biubiu_uuid", uuid);
			editor.commit();
		}
		return uuid;
	}
	public static String setToNoNull(String str) {
		return (str != null) ? str : "";
	}
	/**
	 * checkPermissions
	 * @param context
	 * @param permission
	 * @return true or  false
	 */
	public static boolean checkPermissions(Context context, String permission) {
		PackageManager localPackageManager = context.getPackageManager();

		return localPackageManager.checkPermission(permission,
				context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean checkPhoneState(Context context) {
		PackageManager packageManager = context.getPackageManager();

		if (packageManager.checkPermission(
				"android.permission.READ_PHONE_STATE",
				context.getPackageName()) != 0) {
			return false;
		}
		return true;
	}
	private static String mPath;

	private static void setImgPath() {
		String filename = System.currentTimeMillis() + ".jpg";
		makeRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + "/iU");
		mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iU" + "/" + filename;
	}

	public static String getImgPath() {
		return mPath;
	}

	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
//                file.createNewFile();
				file.mkdir();
			}
		} catch (Exception e) {
		}
	}
	public static void startPhotoZoom(Context con, Uri uri,int resultCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		// intent.putExtra("crop", "true");
		setImgPath();
		File finalFile = new File(getImgPath());
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		// intent.putExtra("outputX", 720);
		// intent.putExtra("outputY", 720);
		intent.putExtra("scale", true);
		// intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(finalFile));
		((Activity)con).startActivityForResult(intent, resultCode);
	}
}
