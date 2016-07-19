package com.android.biubiu.ui.half.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biubiu.ui.half.bean.SquareBean;

import org.xutils.x;

import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * Created by meetu on 2016/7/12.
 */
public class SquareAdapter extends BaseAdapter {
    Context context;
    ArrayList<SquareBean> squares;
    public SquareAdapter(Context context,ArrayList<SquareBean> squares){
        this.context = context;
        this.squares = squares;
    }
    @Override
    public int getCount() {
        return squares.size();
    }

    @Override
    public Object getItem(int position) {
        return squares.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareBean bean = squares.get(position);
        ViewHloder vh = null;
        if(convertView == null){
            vh = new ViewHloder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_square_item,null);
            vh.frountView = convertView.findViewById(R.id.front_view);
            vh.iconImv = (ImageView) convertView.findViewById(R.id.icon_imv);
            vh.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            vh.countTv = (TextView) convertView.findViewById(R.id.count_tv);
            vh.descirptionTv = (TextView) convertView.findViewById(R.id.description_tv);
            convertView.setTag(vh);
        }else{
            vh = (ViewHloder) convertView.getTag();
        }
        vh.frountView.setBackgroundColor(Color.parseColor("#"+bean.getColor()));
        x.image().bind(vh.iconImv,bean.getIconUrl());
        vh.nameTv.setText(bean.getContent());
        vh.countTv.setText(bean.getNumResponse()+"");
        vh.descirptionTv.setText("已经产生"+bean.getNumDuty()+"条记录");
        return convertView;
    }
    class ViewHloder{
        View frountView;
        ImageView iconImv;
        TextView nameTv;
        TextView countTv;
        TextView descirptionTv;
    }
}
