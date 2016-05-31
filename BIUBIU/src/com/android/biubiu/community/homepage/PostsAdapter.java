package com.android.biubiu.community.homepage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.DateUtils;

import org.xutils.x;

import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/5/31.
 */
public class PostsAdapter extends BaseAdapter {
    private List<Posts> mData;
    private Context mCon;
    private LayoutInflater mInflater;
    private SchoolDao schoolDao;
    public PostsAdapter(List<Posts> mData, Context mCon) {
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
        Posts posts = mData.get(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.posts_item_layout, parent,false);
            vh.head = (ImageView) convertView.findViewById(R.id.head_imageview);
            vh.nickname = (TextView) convertView.findViewById(R.id.nickname_textview);
            vh.school = (TextView) convertView.findViewById(R.id.school_textview);
            vh.more = (ImageView) convertView.findViewById(R.id.more_img);
            vh.time = (TextView) convertView.findViewById(R.id.time_textview);

            vh.imgLayout = (LinearLayout) convertView.findViewById(R.id.image_layout);
            vh.tag = (TextView) convertView.findViewById(R.id.tag_textview);
            vh.content = (TextView) convertView.findViewById(R.id.content_textview);
            vh.praise = (TextView) convertView.findViewById(R.id.praise_num_textview);
            vh.comment = (TextView) convertView.findViewById(R.id.comment_num_textview);
            vh.praiseImg = (ImageView) convertView.findViewById(R.id.praise_imageview);
            vh.commentImg = (ImageView) convertView.findViewById(R.id.comment_imageview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        x.image().bind(vh.head,posts.getUserHead());
        vh.nickname.setText(posts.getUserName());
        if(!TextUtils.isEmpty(posts.getUserSchool())){
            if (schoolDao.getschoolName(posts.getUserSchool()).get(0).getUnivsNameString() != null) {
                vh.school.setText(schoolDao.getschoolName(posts.getUserSchool()).get(0).getUnivsNameString());
            }
        }
        vh.time.setText(DateUtils.getDateFormatInList(mCon, posts.getCreateAt()));
        vh.tag.setText(mCon.getResources().getString(R.string.tag,posts.getTags().get(0).getContent()));
        vh.content.setText(posts.getContent());
        vh.praise.setText(mCon.getResources().getString(R.string.praise_num,posts.getPraiseNum()));
        vh.comment.setText(mCon.getResources().getString(R.string.comment_num,posts.getCommentNum()));
        vh.praiseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        vh.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView head;
        TextView nickname,school,time;
        ImageView more;
        LinearLayout imgLayout;
        TextView tag,content,praise,comment;
        ImageView praiseImg,commentImg;
    }
}
