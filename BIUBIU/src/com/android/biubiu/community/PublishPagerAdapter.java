package com.android.biubiu.community;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.biubiu.activity.mine.UserPhotoScanActivity;
import com.android.biubiu.bean.UserPhotoBean;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.DisplayUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * Created by meetu on 2016/5/30.
 */
public class PublishPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> photos;
    ImageOptions options;
    boolean isMyself;
    public PublishPagerAdapter(Context context,ArrayList<String> photos,ImageOptions options) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.photos = photos;
        this.options = options;

    }
    @Override
    public float getPageWidth(int position) {
        float pagerWidth = DisplayUtils.getWindowWidth(context)- DensityUtil.dip2px(context, 100);
        float scale = DensityUtil.dip2px(context, 70)/pagerWidth;
        return  scale;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return photos.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView((View)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // TODO Auto-generated method stub
        View itemView = LayoutInflater.from(context).inflate(R.layout.userpager_photo_item, null);
        container.addView(itemView);
        ImageView imv = (ImageView) itemView.findViewById(R.id.userphoto_imv);
        x.image().bind(imv, photos.get(position),options);
        return itemView;
    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

}
