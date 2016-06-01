package com.android.biubiu.community.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.bean.community.Img;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.DateUtils;

import org.xutils.x;

import java.util.ArrayList;
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
        final Posts posts = mData.get(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.posts_item_layout, parent, false);
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
        x.image().bind(vh.head, posts.getUserHead());
        vh.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPagerActivity(posts);
            }
        });
        vh.nickname.setText(posts.getUserName());
        if("1".equals(posts.getUserSex())){
            vh.nickname.setTextColor(Color.parseColor("#8883bc"));
        }else if("2".equals(posts.getUserSex())){
            vh.nickname.setTextColor(Color.parseColor("#f0637f"));
        }
        vh.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPagerActivity(posts);
            }
        });
        if (!TextUtils.isEmpty(posts.getUserSchool())) {
            if (schoolDao.getschoolName(posts.getUserSchool()).get(0).getUnivsNameString() != null) {
                vh.school.setText(schoolDao.getschoolName(posts.getUserSchool()).get(0).getUnivsNameString());
            }
        }
        vh.time.setText(DateUtils.getDateFormatInList(mCon, posts.getCreateAt() * 1000));
        setPic(vh, posts);
        if (posts.getTags() != null && posts.getTags().size() > 0) {
            vh.tag.setText(mCon.getResources().getString(R.string.tag, posts.getTags().get(0).getContent()));
        }
        vh.content.setText(posts.getContent());
        vh.praise.setText(mCon.getResources().getString(R.string.praise_num, posts.getPraiseNum()));
        vh.comment.setText(mCon.getResources().getString(R.string.comment_num, posts.getCommentNum()));
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

    private void toPagerActivity(Posts posts) {
        Intent intent=new Intent(mCon,MyPagerActivity.class);
        intent.putExtra("userCode", String.valueOf(posts.getUserCode()));
        mCon.startActivity(intent);
    }

    private void setPic(ViewHolder vh, Posts posts) {
        if (vh.imgLayout.getChildCount() > 0) {
            vh.imgLayout.removeAllViews();
        }
        List<Img> imgs = posts.getImgs();
        if (imgs != null && imgs.size() > 0) {
            vh.imgLayout.setVisibility(View.VISIBLE);
            int size = imgs.size();
            if (size == 1) {//只有一张图片
                Img img = imgs.get(0);
                ImageView iv = new ImageView(mCon);
                if (img.getW() != 0 && img.getH() != 0) {
                    x.image().bind(iv, packageUrl(img.getW(), img.getH(), mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic), img.getUrl()));
                } else {
                    x.image().bind(iv, img.getUrl());
                }
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic),
                        mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic));
                vh.imgLayout.addView(iv, params);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                int rows = size / 3;
                if (rows == 0) {//只有一行
                    LinearLayout rowLayout = new LinearLayout(mCon);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    for (int i = 0; i < 2; i++) {
                        addImgView(imgs.get(i), i != 0, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_two_pic));
                    }
                    LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic),
                            mCon.getResources().getDimensionPixelSize(R.dimen.post_list_two_pic));
                    vh.imgLayout.addView(rowLayout, rowParam);
                } else {//多行
                    if (size % 3 == 0) {//正好是3的倍数
                        for (int i = 0; i < rows; i++) {
                            LinearLayout rowLayout = new LinearLayout(mCon);
                            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                            for (int j = i * 3; j < (i + 1) * 3; j++) {
                                addImgView(imgs.get(j), j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                            }
                            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic),
                                    mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                            rowParam.setMargins(0, mCon.getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0);
                            vh.imgLayout.addView(rowLayout, rowParam);
                        }
                    } else {//多加一行
                        rows = rows + 1;
                        for (int i = 0; i < rows; i++) {
                            LinearLayout rowLayout = new LinearLayout(mCon);
                            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                            if (i == rows - 1) {
                                for (int j = i * 3; j < (rows - 1) * 3 + size % 3; j++) {
                                    addImgView(imgs.get(j), j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                                }
                            } else {
                                for (int j = i * 3; j < (i + 1) * 3; j++) {
                                    addImgView(imgs.get(j), j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                                }
                            }
                            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mCon.getResources().getDimensionPixelSize(R.dimen.post_list_one_pic),
                                    mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                            rowParam.setMargins(0, mCon.getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0);
                            vh.imgLayout.addView(rowLayout, rowParam);
                        }
                    }
                }
            }
        } else {
            vh.imgLayout.setVisibility(View.GONE);
        }

    }

    private void addImgView(Img img, boolean b, LinearLayout rowLayout, int targetSize) {
        ImageView imageView = new ImageView(mCon);
        x.image().bind(imageView, packageUrl(img.getW(), img.getH(), targetSize, img.getUrl()));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(targetSize, targetSize);
        if (b) {
            params.setMargins(mCon.getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0, 0);
        }
        rowLayout.addView(imageView, params);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private String packageUrl(int imgW, int imgH, int dimensionPixelSize, String url) {
        StringBuilder sb = new StringBuilder(url);
//        if (imgW > dimensionPixelSize || imgH > dimensionPixelSize) {
        sb.append("@").append(dimensionPixelSize + "w").append("_1l");
//        }
        return sb.toString();
    }

    class ViewHolder {
        ImageView head;
        TextView nickname, school, time;
        ImageView more;
        LinearLayout imgLayout;
        TextView tag, content, praise, comment;
        ImageView praiseImg, commentImg;
    }
}
