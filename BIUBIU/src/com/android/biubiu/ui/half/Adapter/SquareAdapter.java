package com.android.biubiu.ui.half.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cc.imeetu.iu.R;

/**
 * Created by meetu on 2016/7/12.
 */
public class SquareAdapter extends BaseAdapter {
    Context context;
    public SquareAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_square_item,null);
        }
        return convertView;
    }
}
