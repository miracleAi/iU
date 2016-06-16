package com.android.biubiu.sqlite;

import com.avos.avoscloud.DeleteCallback;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.StaticLayout;

public class MySqliteDBHelper extends SQLiteOpenHelper {

	private static final int VERSION = 3;

	public MySqliteDBHelper(Context context) {
		super(context, DbConstents.DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer user = new StringBuffer();
		user.append("create table if not exists ");
		user.append(DbConstents.USER_FRIEND + "(");
		user.append(DbConstents.USER_FRIEND_COLUMN_ID + " integer primary key autoincrement ,");
		user.append(DbConstents.USER_FRIEND_STAR_SIGN + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_USER_CODE + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_NICK_NAME + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_SCHOOL + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_IS_GRADUATED + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_SEX + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_CARRER + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_ICON_URL + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_COMPANY + " varchar(100) ,");
		user.append(DbConstents.USER_FRIEND_AGE + " Integer ");
		user.append(")");
		db.execSQL(user.toString());
		//推送缓存
		StringBuffer push = new StringBuffer();
		push.append("create table if not exists ");
		push.append(DbConstents.PUSH_MATCH + "(");
		push.append(DbConstents.PUSH_USER_CODE + " varchar(100) primary key,");
		push.append(DbConstents.PUSH_TIME + " Integer ");
		push.append(")");
		db.execSQL(push.toString());
		//biubiu列表缓存
		StringBuffer biuStr = new StringBuffer();
		biuStr.append("create table if not exists ");
		biuStr.append(DbConstents.BIU_LIST_DB + "(");
		biuStr.append(DbConstents.USER_CODE + " Integer primary key,");
		biuStr.append(DbConstents.ICON_THUM_URL + " varchar(100), ");
		biuStr.append(DbConstents.NICKNAME + " varchar(100), ");
		biuStr.append(DbConstents.SEX + " varchar(100), ");
		biuStr.append(DbConstents.AGE + " Integer, ");
		biuStr.append(DbConstents.STARSIGN + " varchar(100), ");
		biuStr.append(DbConstents.SCHOOL + " varchar(100), ");
		biuStr.append(DbConstents.MACH_SCORE + " Integer, ");
		biuStr.append(DbConstents.DISTANCE + " Integer, ");
		biuStr.append(DbConstents.TIME + " Integer, ");
		biuStr.append(DbConstents.IS_READ + " varchar(100) ");
		biuStr.append(")");
		db.execSQL(biuStr.toString());

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		switch (oldVersion) {
		case 1:
			StringBuffer push = new StringBuffer();
			push.append("create table if not exists ");
			push.append(DbConstents.PUSH_MATCH + "(");
			push.append(DbConstents.PUSH_USER_CODE + " varchar(100) primary key,");
			push.append(DbConstents.PUSH_TIME + " Integer ");
			push.append(")");
			db.execSQL(push.toString());
			break;
			case 2:
				StringBuffer biuStr = new StringBuffer();
				biuStr.append("create table if not exists ");
				biuStr.append(DbConstents.BIU_LIST_DB + "(");
				biuStr.append(DbConstents.USER_CODE + " Integer primary key,");
				biuStr.append(DbConstents.ICON_THUM_URL + " varchar(100), ");
				biuStr.append(DbConstents.NICKNAME + " varchar(100), ");
				biuStr.append(DbConstents.SEX + " varchar(100), ");
				biuStr.append(DbConstents.AGE + " Integer, ");
				biuStr.append(DbConstents.STARSIGN + " varchar(100), ");
				biuStr.append(DbConstents.SCHOOL + " varchar(100), ");
				biuStr.append(DbConstents.MACH_SCORE + " Integer, ");
				biuStr.append(DbConstents.DISTANCE + " Integer, ");
				biuStr.append(DbConstents.TIME + " Integer, ");
				biuStr.append(DbConstents.IS_READ + " varchar(100) ");
				biuStr.append(")");
				db.execSQL(biuStr.toString());
				break;
		}
	}

}
