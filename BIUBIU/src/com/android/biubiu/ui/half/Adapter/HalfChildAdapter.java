package com.android.biubiu.ui.half.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biubiu.common.Constant;
import com.android.biubiu.component.util.Constants;
import com.android.biubiu.component.util.DateUtils;
import com.android.biubiu.component.util.Utils;
import com.android.biubiu.ui.half.bean.HalfUserBean;

import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * Created by meetu on 2016/7/12.
 */
public class HalfChildAdapter extends BaseAdapter {
    Context context;
    ArrayList<HalfUserBean> userList;

    public HalfChildAdapter(Context context, ArrayList<HalfUserBean> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HalfUserBean bean = userList.get(position);
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_half_child_item, null);
            vh.headImv = (ImageView) convertView.findViewById(R.id.head_imv);
            vh.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            vh.distanceTv = (TextView) convertView.findViewById(R.id.distance_tv);
            vh.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
            vh.schoolTv = (TextView) convertView.findViewById(R.id.school_tv);
            vh.signTv = (TextView) convertView.findViewById(R.id.description_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        x.image().bind(vh.headImv, bean.getIcon_thumbnailUrl());
        vh.nameTv.setText(bean.getNickname());
        String distance = new DecimalFormat("#00").format(bean.getDistance() / 1000);
        vh.distanceTv.setText(distance + "km");
        vh.timeTv.setText(DateUtils.getDateFormatInList(context,bean.getActytime()));
        vh.schoolTv.setText(bean.getSchool());
        vh.signTv.setText(bean.getSign());
        if (bean.getSex().equals(Constants.SEX_FAMALE)) {
            vh.schoolTv.setCompoundDrawables(context.getResources().getDrawable(R.drawable.list_icon_girl), null, null, null);
        } else {
            vh.schoolTv.setCompoundDrawables(context.getResources().getDrawable(R.drawable.list_icon_boy), null, null, null);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView headImv;
        TextView nameTv;
        TextView distanceTv;
        TextView timeTv;
        TextView schoolTv;
        TextView signTv;

    }
}
