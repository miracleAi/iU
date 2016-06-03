package com.android.biubiu.community.homepage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.activity.mine.UserPhotoScanActivity;
import com.android.biubiu.bean.UserPhotoBean;
import com.android.biubiu.bean.base.Data;
import com.android.biubiu.bean.community.Img;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.bean.community.PraiseData;
import com.android.biubiu.bean.community.SimpleRespData;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.DateUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
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
    private int mUserCode;
    private IRefreshUi mRefreshUi;
    private boolean mIsTagPostListPage;
    public PostsAdapter(List<Posts> mData, Context mCon) {
        this.mData = mData;
        this.mCon = mCon;
        mInflater = LayoutInflater.from(mCon);
        schoolDao = new SchoolDao();
        mUserCode = Integer.parseInt(SharePreferanceUtils.getInstance().getUserCode(mCon, SharePreferanceUtils.USER_CODE, ""));
    }

    public interface IRefreshUi {
        void whenDelete(Posts posts);
    }

    public void setIRefreshUi(IRefreshUi refreshUi){
        mRefreshUi = refreshUi;
    }

    public void setIsTagPostListPage(boolean isTagPostListPage){
        mIsTagPostListPage = isTagPostListPage;
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
            vh.time = (TextView) convertView.findViewById(R.id.time_textview);

            vh.reportLayout = (LinearLayout) convertView.findViewById(R.id.more_layout);
            vh.imgLayout = (LinearLayout) convertView.findViewById(R.id.image_layout);
            vh.tag = (TextView) convertView.findViewById(R.id.tag_textview);
            vh.content = (TextView) convertView.findViewById(R.id.content_textview);
            vh.praise = (TextView) convertView.findViewById(R.id.praise_num_textview);
            vh.comment = (TextView) convertView.findViewById(R.id.comment_num_textview);
            vh.praiseImg = (ImageView) convertView.findViewById(R.id.praise_imageview);
//            vh.commentImg = (ImageView) convertView.findViewById(R.id.comment_imageview);
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
        if ("1".equals(posts.getUserSex())) {
            vh.nickname.setTextColor(Color.parseColor("#8883bc"));
        } else if ("2".equals(posts.getUserSex())) {
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
        if(!mIsTagPostListPage){
            vh.tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mCon,PostsListByTagActivity.class);
                    i.putExtra(Constant.TAG,posts.getTags().get(0));
                    mCon.startActivity(i);
                }
            });
        }
        vh.content.setText(posts.getContent());
        vh.praise.setText(mCon.getResources().getString(R.string.praise_num, posts.getPraiseNum()));
        vh.comment.setText(mCon.getResources().getString(R.string.comment_num, posts.getCommentNum()));
        if (posts.getIsPraise() == 1) {
            vh.praiseImg.setImageResource(R.drawable.found_btn_like_light);
        } else {
            vh.praiseImg.setImageResource(R.drawable.found_btn_like_normal);
        }
        vh.praiseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praise(posts);
            }
        });
        vh.reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOperation(mUserCode == posts.getUserCode(), posts);
            }
        });
        return convertView;
    }

    private void toPagerActivity(Posts posts) {
        Intent intent = new Intent(mCon, MyPagerActivity.class);
        intent.putExtra("userCode", String.valueOf(posts.getUserCode()));
        mCon.startActivity(intent);
    }

    private void setPic(ViewHolder vh, final Posts posts) {
        if (vh.imgLayout.getChildCount() > 0) {
            vh.imgLayout.removeAllViews();
        }
        final List<Img> imgs = posts.getImgs();
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
                        toPreviewPage(0, imgs);
                    }
                });
            } else {
                int rows = size / 3;
                if (rows == 0) {//只有一行
                    LinearLayout rowLayout = new LinearLayout(mCon);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    for (int i = 0; i < 2; i++) {
                        addImgView(i, imgs, i != 0, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_two_pic));
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
                                addImgView(j, imgs, j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
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
                                    addImgView(j, imgs, j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                                }
                            } else {
                                for (int j = i * 3; j < (i + 1) * 3; j++) {
                                    addImgView(j, imgs, j != i * 3, rowLayout, mCon.getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
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

    private void toPreviewPage(int position, List<Img> imgs) {
        ArrayList<UserPhotoBean> photos = new ArrayList<UserPhotoBean>();
        for (Img img : imgs) {
            UserPhotoBean bean = new UserPhotoBean();
            bean.setPhotoOrigin(img.getUrl());
            photos.add(bean);
        }
        Intent intent = new Intent(mCon, UserPhotoScanActivity.class);
        intent.putExtra("photolist", photos);
        intent.putExtra("photoindex", position);
        intent.putExtra("isMyself", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCon.startActivity(intent);
    }

    private void addImgView(final int index, final List<Img> imgs, boolean b, LinearLayout rowLayout, int targetSize) {
        ImageView imageView = new ImageView(mCon);
        x.image().bind(imageView, packageUrl(imgs.get(index).getW(), imgs.get(index).getH(), targetSize, imgs.get(index).getUrl()));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(targetSize, targetSize);
        if (b) {
            params.setMargins(mCon.getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0, 0);
        }
        rowLayout.addView(imageView, params);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPreviewPage(index, imgs);
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
        LinearLayout imgLayout, reportLayout;
        TextView tag, content, praise, comment;
        ImageView praiseImg/*, commentImg*/;
    }

    private void praise(final Posts posts) {
        RequestParams params = new RequestParams(HttpContants.PRAISE_DOPRAISE);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", posts.getPostId());
            requestObject.put("userCode", posts.getUserCode());
            if (posts.getIsPraise() == 1) {
                requestObject.put("praise", 0);
            } else if (posts.getIsPraise() == 0) {
                requestObject.put("praise", 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<PraiseData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<PraiseData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), mCon)) {
                    return;
                }
                PraiseData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                if (posts.getIsPraise() == 1) {
                    posts.setIsPraise(0);
                } else if (posts.getIsPraise() == 0) {
                    posts.setIsPraise(1);
                }
                posts.setPraiseNum(data.getPraiseNum());
                notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 举报或删除
     */
    private void showOperation(final boolean b, final Posts posts) {
        final AlertDialog portraidlg = new AlertDialog.Builder(mCon).create();
        portraidlg.show();

        Window win = portraidlg.getWindow();
        win.setContentView(R.layout.item_hint_moster_dralog_mypage);
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(params);

        RelativeLayout dismissLayout, jubaoLayout;
        jubaoLayout = (RelativeLayout) win.findViewById(R.id.jubao_dialog_mupage_rl);
        dismissLayout = (RelativeLayout) win.findViewById(R.id.dismiss_dialog_mypage_rl);
        dismissLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                portraidlg.dismiss();
            }
        });
        if (b) {
            ((TextView) jubaoLayout.findViewById(R.id.operation_textview)).setText(mCon.getResources().getString(R.string.delete));
        }
        jubaoLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String tips, title;
                if (b) {
                    title = mCon.getResources().getString(R.string.delete_title);
                    tips = mCon.getResources().getString(R.string.delete_tips);
                } else {
                    title = mCon.getResources().getString(R.string.report_title);
                    tips = mCon.getResources().getString(R.string.report_tips);
                }
                portraidlg.dismiss();
                MyHintDialog.getDialog(mCon, title, tips, mCon.getResources().getString(R.string.sure),
                        new MyHintDialog.OnDialogClick() {

                            @Override
                            public void onOK() {
                                if (b) {
                                    deletePost(posts);
                                } else {
                                    reportPost(posts);
                                }
                            }

                            @Override
                            public void onDismiss() {

                            }
                        });
            }
        });
    }

    private void deletePost(final Posts posts) {
        RequestParams params = new RequestParams(HttpContants.POST_DELETEPOST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", posts.getPostId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), mCon)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(mCon, mCon.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                //刷新数据源
                if (mRefreshUi != null) {
                    mRefreshUi.whenDelete(posts);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void reportPost(Posts posts) {
        RequestParams params = new RequestParams(HttpContants.REPORT_CREATEREPORT);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", posts.getPostId());
            requestObject.put("commentId", 0);
            requestObject.put("userCode", posts.getUserCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), mCon)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(mCon, mCon.getResources().getString(R.string.report_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
