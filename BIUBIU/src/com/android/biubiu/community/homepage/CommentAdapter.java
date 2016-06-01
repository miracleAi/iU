package com.android.biubiu.community.homepage;

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
import com.android.biubiu.bean.community.Comment;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.DateUtils;

import org.xutils.x;

import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/6/1.
 */
public class CommentAdapter extends BaseAdapter {
    private List<Comment> mData;
    private Context mCon;
    private LayoutInflater mInflater;
    private SchoolDao schoolDao;

    public CommentAdapter(List<Comment> mData, Context mCon) {
        this.mData = mData;
        this.mCon = mCon;
        mInflater = LayoutInflater.from(mCon);
        schoolDao = new SchoolDao();
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
        final Comment comment = mData.get(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comment_item_layout, parent, false);
            vh.head = (ImageView) convertView.findViewById(R.id.head_imageview);
            vh.more = (ImageView) convertView.findViewById(R.id.more_img);
            vh.nickname = (TextView) convertView.findViewById(R.id.nickname_textview);
            vh.school = (TextView) convertView.findViewById(R.id.school_textview);
            vh.time = (TextView) convertView.findViewById(R.id.time_textview);
            vh.content = (TextView) convertView.findViewById(R.id.content_textview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        x.image().bind(vh.head, comment.getUserFromHead());
        vh.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mCon,MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(comment.getUserFromCode()));
                mCon.startActivity(intent);
            }
        });
        vh.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mCon,MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(comment.getUserFromCode()));
                mCon.startActivity(intent);
            }
        });
        vh.nickname.setText(comment.getUserFromName());
        if("1".equals(comment.getUserFromSex())){
            vh.nickname.setTextColor(Color.parseColor("#8883bc"));
        }else if("2".equals(comment.getUserFromSex())){
            vh.nickname.setTextColor(Color.parseColor("#f0637f"));
        }
        if (!TextUtils.isEmpty(comment.getUserFromSchool())) {
            if (schoolDao.getschoolName(comment.getUserFromSchool()).get(0).getUnivsNameString() != null) {
                vh.school.setText(schoolDao.getschoolName(comment.getUserFromSchool()).get(0).getUnivsNameString());
            }
        }
        vh.time.setText(DateUtils.getDateFormatInList(mCon, comment.getCreateAt() * 1000));
        int parentId = comment.getParentId();
        if (parentId != 0) {
            vh.content.setText(mCon.getResources().getString(R.string.reply_comment, comment.getUserToName(),
                    comment.getContent()));
        } else {
            vh.content.setText(comment.getContent());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView head, more;
        TextView nickname, school, time, content;
    }
}
