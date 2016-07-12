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
public class HalfChildAdapter extends BaseAdapter{
    Context context;
    public HalfChildAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_half_child_item,null);
        }
        return convertView;
    }
}
