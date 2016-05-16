package com.android.biubiu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.biubiu.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meetu on 2016/5/16.
 */
public class BiubiuDao {
    private MySqliteDBHelper dbHelper;

    public BiubiuDao(Context context) {
        // TODO Auto-generated constructor stub
        dbHelper = new MySqliteDBHelper(context);
    }
    /**
     *
     * 删除所有的biubiu缓存
     */
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "delete from "+DbConstents.BIU_LIST_DB;
        db.execSQL(sql);
        db.close();

    }
    /**
     *
     * 删除指定用户的biubiu缓存
     */
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
                        + "where " + DbConstents.IS_READ + "=? ",
                new String[]{"0"});
        List<UserBean> list = new ArrayList<UserBean>();
        while (c.moveToNext()) {
            UserBean bean = new UserBean();
            list.add(bean);
        }
        c.close();
        db.close();
        if(null != list){
            return list.size();
        }else{
            return 0;
        }
    }
    /**
     * 查询一条时间最新且没有显示过得biubiu
     * */
    public UserBean getBiuToShow(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "
                + DbConstents.BIU_LIST_DB + " where " + DbConstents.IS_READ
                + "=? order by " + DbConstents.TIME
                + " asc", new String[] { "0" });
        List<UserBean> list = new ArrayList<UserBean>();
        while (c.moveToNext()) {
            UserBean bean = new UserBean();
            list.add(bean);
        }
        c.close();
        db.close();
        if(null != list && list.size()>0){
            UserBean bean = list.get(0);
            return bean;
        }else{
            return null;
        }
    }
    /**
     * 更新biubiu的状态为已读
     */
    public void updateBiuState(String userCode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbConstents.IS_READ, "1");
        db.update(DbConstents.BIU_LIST_DB, values, DbConstents.USER_CODE
                + "=?", new String[] {userCode });
        db.close();
    }
    /**
     * 更新biubiu列表的状态为未读
     */
    public void updateAllBiuState( ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update "+DbConstents.BIU_LIST_DB+" set "+DbConstents.IS_READ+"=?",new String[]{"0"});
        db.close();
    }
    /**
     * 插入一条biubiu数据
     */
    public void addOneBiu(UserBean bean){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(
                "insert or replace into "+DbConstents.BIU_LIST_DB+" values("+"?,?,?,?,?,"
                        + "?,?,?,?,?,?)",
                new Object[] { });
        db.close();
    }
    /**
     * 插入biubiu列表
     * */
    public void addBiuList(ArrayList<UserBean> biuList){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(int i=0;i<biuList.size();i++){
            UserBean bean = biuList.get(i);
            ContentValues cv = new ContentValues();

            db.insert(DbConstents.BIU_LIST_DB, null, cv);
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
        List<UserBean> list = new ArrayList<UserBean>();
        while (c.moveToNext()) {
            UserBean bean = new UserBean();
            list.add(bean);
        }
        c.close();
        db.close();
        if(null != list && list.size()>0){
            UserBean bean = list.get(0);
            return bean.getTime();
        }else{
            return 0;
        }
    }
}
