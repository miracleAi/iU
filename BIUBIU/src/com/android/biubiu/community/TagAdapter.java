package com.android.biubiu.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.biubiu.bean.TagBean;

import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * Created by meetu on 2016/5/31.
 */
public class TagAdapter extends BaseAdapter {
    Context context;
    ArrayList<TagBean> list;
    public  TagAdapter(Context context,ArrayList<TagBean> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TagBean bean = list.get(position);
        ViewHolder vh = null;
        if(convertView == null ){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tag_adapter_item,null);
            vh.tagTv = (TextView) convertView.findViewById(R.id.tag_tv);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tagTv.setText(bean.getContent());
        return convertView;
    }
    class ViewHolder{
        TextView tagTv;
    }
}
