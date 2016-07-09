package com.android.biubiu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.component.util.Constants;
import com.android.biubiu.persistence.base.DbConstents;
import com.android.biubiu.persistence.base.MySqliteDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meetu on 2016/5/16.*/


public class BiubiuDao {
    private MySqliteDBHelper dbHelper;

    public BiubiuDao(Context context) {
        // TODO Auto-generated constructor stub
        dbHelper = new MySqliteDBHelper(context);
    }
/**
     *
     * 删除所有的biubiu缓存*/


    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "delete from "+ DbConstents.BIU_LIST_DB;
        db.execSQL(sql);
        db.close();

    }
/**
     *
     * 删除指定用户的biubiu缓存*/


    public void deleteByUserCode(int userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "delete from chatmsgs where "+DbConstents.USER_CODE+"=? ";
        db.execSQL(sql, new Object[] {userCode });
        db.close();
    }
/**
     * 查询没有显示过得biubiu列表
     * */

    public int getBiuListUnread() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DbConstents.BIU_LIST_DB
                        + " where " + DbConstents.IS_READ + "=? ",
                new String[]{Constants.BIU_UNREAD});
        int i=0;
        while (c.moveToNext()) {
            i =i+1;
        }
        c.close();
        db.close();
        return i;
    }
/**
     * 查询一条时间最新且没有显示过得biubiu
     * */

    public BiuBean getBiuToShow(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "
                + DbConstents.BIU_LIST_DB + " where " + DbConstents.IS_READ
                + "=? order by " + DbConstents.TIME
                + " desc", new String[] { Constants.BIU_UNREAD });
        List<BiuBean> list = new ArrayList<BiuBean>();
        while (c.moveToNext()) {
            BiuBean bean = new BiuBean();
            bean.setIconUrl(c.getString(c.getColumnIndex(DbConstents.ICON_THUM_URL)));
            bean.setUserCode(c.getInt(c.getColumnIndex(DbConstents.USER_CODE)));
            bean.setAge(c.getInt(c.getColumnIndex(DbConstents.AGE)));
            bean.setDistance(c.getInt(c.getColumnIndex(DbConstents.DISTANCE)));
            bean.setMatchScore(c.getInt(c.getColumnIndex(DbConstents.MACH_SCORE)));
            bean.setIsRead(c.getString(c.getColumnIndex(DbConstents.IS_READ)));
            bean.setNickname(c.getString(c.getColumnIndex(DbConstents.NICKNAME)));
            bean.setSchool(c.getString(c.getColumnIndex(DbConstents.SCHOOL)));
            bean.setSex(c.getString(c.getColumnIndex(DbConstents.SEX)));
            bean.setStarsign(c.getString(c.getColumnIndex(DbConstents.STARSIGN)));
            bean.setTime(c.getLong(c.getColumnIndex(DbConstents.TIME)));
            list.add(bean);
        }
        c.close();
        db.close();
        if(null != list && list.size()>0){
            BiuBean bean = list.get(0);
            return bean;
        }else{
            return null;
        }
    }
/**
     * 更新biubiu的状态为已读
     */

    public void updateBiuState(int userCode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.execSQL("update "+DbConstents.BIU_LIST_DB+" set "+DbConstents.IS_READ+"="+ Constants.BIU_READ+
                " where "+DbConstents.USER_CODE+"=? ",new Integer[]{userCode});
        values.put(DbConstents.IS_READ, Constants.BIU_READ);
        db.close();
    }
/**
     * 更新biubiu列表的状态为未读*/


 public void updateAllBiuState( ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update "+DbConstents.BIU_LIST_DB+" set "+DbConstents.IS_READ+"=?",new String[]{Constants.BIU_UNREAD});
        db.close();
    }

/**
     * 插入一条biubiu数据*/


    public void addOneBiu(BiuBean bean,String receiveSex){
        if(bean.getSex().equals(receiveSex)){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(
                    "insert or replace into "+DbConstents.BIU_LIST_DB+" values("+"?,?,?,?,?,"
                            + "?,?,?,?,?,?)",
                    new Object[] {bean.getUserCode(),bean.getIconUrl(),bean.getNickname(),bean.getSex(),bean.getAge(),
                            bean.getStarsign(),bean.getSchool(),bean.getMatchScore(),bean.getDistance(),bean.getTime(),Constants.BIU_UNREAD});
            db.close();
        }
    }
/**
     * 插入biubiu列表
     * */

    public void addBiuList(ArrayList<BiuBean> biuList,String receiveSex){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(int i=0;i<biuList.size();i++){
            BiuBean bean = biuList.get(i);
            if(bean.getSex().equals(receiveSex)){
                db.execSQL(
                        "insert or replace into "+DbConstents.BIU_LIST_DB+" values("+"?,?,?,?,?,"
                                + "?,?,?,?,?,?)",
                        new Object[] {bean.getUserCode(),bean.getIconUrl(),bean.getNickname(),bean.getSex(),bean.getAge(),
                                bean.getStarsign(),bean.getSchool(),bean.getMatchScore(),bean.getDistance(),bean.getTime(),Constants.BIU_UNREAD});
            }
        }
        db.close();
    }
/**
     * 查询最后一条biubiu的时间
     * */

    public long getLastBiuTime(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "
                + DbConstents.BIU_LIST_DB + " where "+"order by " + DbConstents.TIME
                + " desc", null);
        List<BiuBean> list = new ArrayList<BiuBean>();
        while (c.moveToNext()) {
            BiuBean bean = new BiuBean();
            bean.setIconUrl(c.getString(c.getColumnIndex(DbConstents.ICON_THUM_URL)));
            bean.setUserCode(c.getInt(c.getColumnIndex(DbConstents.USER_CODE)));
            bean.setAge(c.getInt(c.getColumnIndex(DbConstents.AGE)));
            bean.setDistance(c.getInt(c.getColumnIndex(DbConstents.DISTANCE)));
            bean.setMatchScore(c.getInt(c.getColumnIndex(DbConstents.MACH_SCORE)));
            bean.setIsRead(c.getString(c.getColumnIndex(DbConstents.IS_READ)));
            bean.setNickname(c.getString(c.getColumnIndex(DbConstents.NICKNAME)));
            bean.setSchool(c.getString(c.getColumnIndex(DbConstents.SCHOOL)));
            bean.setSex(c.getString(c.getColumnIndex(DbConstents.SEX)));
            bean.setStarsign(c.getString(c.getColumnIndex(DbConstents.STARSIGN)));
            bean.setTime(c.getLong(c.getColumnIndex(DbConstents.TIME)));
            list.add(bean);
        }
        c.close();
        db.close();
        if(null != list && list.size()>0){
            BiuBean bean = list.get(0);
            return bean.getTime();
        }else{
            return 0;
        }
    }
}
