package com.android.biubiu.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.transport.http.response.community.CommNotify;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.DateUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/6/3.
 */
public class CommNotifyAdapter extends BaseAdapter {
    private List<CommNotify> mData = new ArrayList<CommNotify>();
    private LayoutInflater mInflater;
    private Context mCon;
    private SchoolDao schoolDao;
    private ImageOptions imageOptions;

    public CommNotifyAdapter(List<CommNotify> mData, Context mCon) {
        this.mData = mData;
        this.mCon = mCon;
        mInflater = LayoutInflater.from(mCon);
        schoolDao = new SchoolDao();
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)

                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(true)
                .build();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        final CommNotify notify = mData.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comm_notify_item_layout, parent, false);
            vh.head = (ImageView) convertView.findViewById(R.id.head_imageview);
            vh.nickname = (TextView) convertView.findViewById(R.id.nickname_textview);
            vh.school = (TextView) convertView.findViewById(R.id.school_textview);
            vh.time = (TextView) convertView.findViewById(R.id.time_textview);
            vh.content = (TextView) convertView.findViewById(R.id.content_textview);
            vh.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_imageview);
            vh.brief = (TextView) convertView.findViewById(R.id.brief_textview);
            vh.unread = (ImageView) convertView.findViewById(R.id.unread_imageview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        x.image().bind(vh.head, notify.getUserHead(), imageOptions);
        vh.nickname.setText(notify.getUserName());
        if ("1".equals(notify.getUserSex())) {
            vh.nickname.setTextColor(Color.parseColor("#8883bc"));
        } else if ("2".equals(notify.getUserSex())) {
            vh.nickname.setTextColor(Color.parseColor("#f0637f"));
        }
        if (notify.getIsRead() == 0) {
            vh.unread.setVisibility(View.VISIBLE);
        } else {
            vh.unread.setVisibility(View.GONE);
        }
        vh.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCon, MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(notify.getUserCode()));
                mCon.startActivity(intent);
            }
        });
        vh.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCon, MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(notify.getUserCode()));
                mCon.startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(notify.getUserSchool())) {
            if (schoolDao.getschoolName(notify.getUserSchool()).get(0).getUnivsNameString() != null) {
                vh.school.setText(schoolDao.getschoolName(notify.getUserSchool()).get(0).getUnivsNameString());
            }
        }
        vh.time.setText(DateUtils.getDateFormatInList2(mCon, notify.getCreateAt() * 1000));
        vh.content.setText(notify.getDesc());
        x.image().bind(vh.thumbnail, notify.getPostImg());
        vh.brief.setText(notify.getPostContent());
        return convertView;
    }

    class ViewHolder {
        ImageView head, thumbnail, unread;
        TextView nickname, school, time, content, brief;
    }
}
