package com.android.biubiu.community.homepage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.activity.mine.UserPhotoScanActivity;
import com.android.biubiu.bean.UserPhotoBean;
import com.android.biubiu.bean.base.Data;
import com.android.biubiu.bean.community.Comment;
import com.android.biubiu.bean.community.Img;
import com.android.biubiu.bean.community.PostDetailData;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.bean.community.PraiseData;
import com.android.biubiu.bean.community.PublishCommentData;
import com.android.biubiu.bean.community.SimpleRespData;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.DateUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class PostsDetailActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener,
        PullToRefreshBase.OnRefreshListener2<ListView>, CommentAdapter.IRefreshUi,
        BGARefreshLayout.BGARefreshLayoutDelegate{
    private static final String TAG = PostsDetailActivity.class.getSimpleName();
    private Posts mPosts;
    private int mPostsId;
    private TopTitleView mTopTitle;
    private PullToRefreshListView mPTRLV;
    private ListView mListview;
    private CommentAdapter mCommentAdapter;
    private List<Comment> mData = new ArrayList<Comment>();

    private View mHeaderView;
    private ImageView mHeaderImg, mPraiseImg, mCommentImg;
    private TextView mNicknameTv, mSchoolTv, mTimeTv, mTagTv, mContentTv, mPraiseTv, mCommentTv;
    private LinearLayout mImgLayout, mReportLayout;

    private EditText mCommentEt;

    private SchoolDao mSchoolDao;
    private int mHasNext;
    private Comment mReplayComment;
    private int mUserCode;

    private boolean mFromTagPostListPage, mFromCommNotifyPage, mKeyboardShow;
    private int mTargetW;

    private BGARefreshLayout mRefreshLayout;
    private ListView mDataLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_detail);
        initView();
        initData();
    }

    private void initView() {
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.posts_item_layout, null);
        initHeader();
        mTopTitle = (TopTitleView) findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPTRLV = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPTRLV.setMode(PullToRefreshBase.Mode.DISABLED);
        mPTRLV.setOnRefreshListener(this);
        mPTRLV.setScrollingWhileRefreshingEnabled(true);
//        mListview = mPTRLV.getRefreshableView();

        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_listview_refresh);
        mListview = (ListView) findViewById(R.id.lv_listview_data);
        mRefreshLayout.setDelegate(this);

        BGANormalRefreshViewHolder normalRefreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(normalRefreshViewHolder);

        mCommentEt = (EditText) findViewById(R.id.comment_edittext);
        mCommentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                }
                return false;
            }
        });
        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    commentDone();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListview.setOnItemClickListener(this);

    }

    private void commentDone() {
        mCommentEt.getEditableText().clear();
        CommonUtils.hideKeyboard(this);
        mCommentEt.setHint(getResources().getString(R.string.comment_hint));
        if (mReplayComment != null) {
            mReplayComment = null;
        }
    }

    private void sendComment() {
        String comment = mCommentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(comment)) {
            RequestParams params = new RequestParams(HttpContants.COMMENT_CREATECOMMENT);
            JSONObject requestObject = new JSONObject();
            try {
                requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
                requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
                requestObject.put("postId", mPostsId);
                if (mReplayComment != null) {
                    requestObject.put("parentId", mReplayComment.getCommentId());
                    requestObject.put("toUserCode", mReplayComment.getUserFromCode());
                } else {
                    requestObject.put("parentId", 0);
                    requestObject.put("toUserCode", mPosts.getUserCode());
                }
                requestObject.put("content", comment);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.addBodyParameter("data", requestObject.toString());
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    LogUtil.d(TAG, "create comment--" + s);
                    Data<PublishCommentData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<PublishCommentData>>() {
                    });
                    if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsDetailActivity.this)) {
                        return;
                    }
                    PublishCommentData data = response.getData();
                    if (!TextUtils.isEmpty(data.getToken())) {
                        SharePreferanceUtils.getInstance().putShared(PostsDetailActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                    }
                    refreshList(data.getComment());
                    commentDone();
                    Toast.makeText(PostsDetailActivity.this, getResources().getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(this, getResources().getString(R.string.comment_not_null), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshList(Comment comment) {
        mData.add(0, comment);
        mCommentAdapter.notifyDataSetChanged();
        mPosts.setCommentNum(mPosts.getCommentNum() + 1);
        mCommentTv.setText(getResources().getString(R.string.comment_num, mPosts.getCommentNum()));
        Intent i = new Intent();
        i.putExtra(Constant.POSTS, mPosts);
        setResult(Constant.PRAISE_RESULT_CODE, i);
    }

    private void initHeader() {
        mHeaderImg = (ImageView) mHeaderView.findViewById(R.id.head_imageview);
        mReportLayout = (LinearLayout) mHeaderView.findViewById(R.id.more_layout);
        mPraiseImg = (ImageView) mHeaderView.findViewById(R.id.praise_imageview);
        mCommentImg = (ImageView) mHeaderView.findViewById(R.id.comment_imageview);

        mNicknameTv = (TextView) mHeaderView.findViewById(R.id.nickname_textview);
        mSchoolTv = (TextView) mHeaderView.findViewById(R.id.school_textview);
        mTimeTv = (TextView) mHeaderView.findViewById(R.id.time_textview);
        mTagTv = (TextView) mHeaderView.findViewById(R.id.tag_textview);
        mContentTv = (TextView) mHeaderView.findViewById(R.id.content_textview);
        mPraiseTv = (TextView) mHeaderView.findViewById(R.id.praise_num_textview);
        mCommentTv = (TextView) mHeaderView.findViewById(R.id.comment_num_textview);

        mImgLayout = (LinearLayout) mHeaderView.findViewById(R.id.image_layout);
    }

    private void initData() {
        mTargetW = Constant.screenWidth - getResources().getDimensionPixelSize(R.dimen.posts_list_margin);
        mUserCode = Integer.parseInt(SharePreferanceUtils.getInstance().getUserCode(this, SharePreferanceUtils.USER_CODE, ""));
        mSchoolDao = new SchoolDao();
        mFromTagPostListPage = getIntent().getBooleanExtra(Constant.FROM_POSTLIST_BY_TAG, false);
        mFromCommNotifyPage = getIntent().getBooleanExtra(Constant.FROM_COMM_NOTIFY_PAGE, false);
        mPosts = (Posts) getIntent().getSerializableExtra(Constant.POSTS);
        if (mPosts != null) {
            mPostsId = mPosts.getPostId();
//            initHeaderUi();
        } else {
            mPostsId = getIntent().getIntExtra(Constant.POSTS_ID, 0);
        }

        mCommentAdapter = new CommentAdapter(mData, this);
        mCommentAdapter.setPostId(mPostsId);
        mCommentAdapter.setIRefreshUi(this);
        mListview.addHeaderView(mHeaderView);
        mListview.setAdapter(mCommentAdapter);
        getPostsDetail(0);
    }

    private void getPostsDetail(final long time) {
        RequestParams params = new RequestParams(HttpContants.POST_DETAIL);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", mPostsId);
            requestObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtil.d(TAG, "getPostsDetail--" + s);
                Data<PostDetailData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<PostDetailData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsDetailActivity.this)) {
                    return;
                }
                PostDetailData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(PostsDetailActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
//                if (mFromCommNotifyPage) {
                mPosts = data.getPost();
                initHeaderUi();
//                }
                mHasNext = data.getHasNext();
                List<Comment> comment = data.getCommentList();
                if (time == 0) {
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                }
                if (comment != null && comment.size() > 0) {
                    mData.addAll(comment);
                    mCommentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                mPTRLV.onRefreshComplete();
                mRefreshLayout.endRefreshing();
                mRefreshLayout.endLoadingMore();
            }

            @Override
            public void onCancelled(CancelledException e) {
//                mPTRLV.onRefreshComplete();
                mRefreshLayout.endRefreshing();
                mRefreshLayout.endLoadingMore();
            }

            @Override
            public void onFinished() {
//                mPTRLV.onRefreshComplete();
                mRefreshLayout.endRefreshing();
                mRefreshLayout.endLoadingMore();
            }
        });
    }

    private void initHeaderUi() {
        x.image().bind(mHeaderImg, mPosts.getUserHead());
        mHeaderImg.setOnClickListener(this);
        mNicknameTv.setOnClickListener(this);
        mNicknameTv.setText(mPosts.getUserName());
        if ("1".equals(mPosts.getUserSex())) {
            mNicknameTv.setTextColor(Color.parseColor("#8883bc"));
        } else if ("2".equals(mPosts.getUserSex())) {
            mNicknameTv.setTextColor(Color.parseColor("#f0637f"));
        }
        if (!TextUtils.isEmpty(mPosts.getUserSchool())) {
            if (!TextUtils.isEmpty(mSchoolDao.getschoolName(mPosts.getUserSchool()).get(0).getUnivsNameString())) {
                mSchoolTv.setText(mSchoolDao.getschoolName(mPosts.getUserSchool()).get(0).getUnivsNameString());
            }/*else{
                mSchoolTv.setText(getResources().getString(R.string.default_school));
            }*/
        }/*else{
            mSchoolTv.setText(getResources().getString(R.string.default_school));
        }*/
        mReportLayout.setOnClickListener(this);
        mTimeTv.setText(DateUtils.getDateFormatInList2(this, mPosts.getCreateAt() * 1000));
        if (mPosts.getTags() != null && mPosts.getTags().size() > 0) {
            mTagTv.setText(getResources().getString(R.string.tag, mPosts.getTags().get(0).getContent()));
            if (!mFromTagPostListPage) {
                mTagTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PostsDetailActivity.this, PostsListByTagActivity.class);
                        i.putExtra(Constant.TAG, mPosts.getTags().get(0));
                        startActivity(i);
                    }
                });
            }
        }
        mContentTv.setText(mPosts.getContent());
        mPraiseTv.setText(getResources().getString(R.string.praise_num, mPosts.getPraiseNum()));
        mCommentTv.setText(getResources().getString(R.string.comment_num, mPosts.getCommentNum()));
        mPraiseImg.setOnClickListener(this);
        mCommentImg.setOnClickListener(this);
        setPraiseUi(mPosts.getIsPraise());
        setPic();
    }

    private void setPraiseUi(int praise) {
        if (praise == 1) {
            mPraiseImg.setImageResource(R.drawable.found_btn_like_light);
        } else {
            mPraiseImg.setImageResource(R.drawable.found_btn_like_normal);
        }
    }


    private void setPic() {
        if (mImgLayout.getChildCount() > 0) {
            mImgLayout.removeAllViews();
        }
        final List<Img> imgs = mPosts.getImgs();
        if (imgs != null && imgs.size() > 0) {
            mImgLayout.setVisibility(View.VISIBLE);
            int size = imgs.size();
            if (size == 1) {//只有一张图片
                Img img = imgs.get(0);
                ImageView iv = new ImageView(this);
                if (img.getW() != 0 && img.getH() != 0) {
                    x.image().bind(iv, packageUrl(mTargetW, img.getUrl()));
                } else {
                    x.image().bind(iv, img.getUrl());
                }
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mTargetW, mTargetW);
                mImgLayout.addView(iv, params);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toPreviewPage(0, imgs);
                    }
                });
            } else {
                int rows = size / 3;
                if (rows == 0) {//只有一行
                    LinearLayout rowLayout = new LinearLayout(this);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    int targetW = (Constant.screenWidth - getResources().getDimensionPixelSize(R.dimen.posts_list_margin_two)) / 2;
                    for (int i = 0; i < 2; i++) {
                        addImgView(i, imgs, i != 0, rowLayout, targetW);
                    }
                    LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mTargetW,
                            getResources().getDimensionPixelSize(R.dimen.post_list_two_pic));
                    mImgLayout.addView(rowLayout, rowParam);
                } else {//多行
                    int targetW = (Constant.screenWidth - getResources().getDimensionPixelSize(R.dimen.posts_list_margin_three)) / 3;
                    if (size % 3 == 0) {//正好是3的倍数
                        for (int i = 0; i < rows; i++) {
                            LinearLayout rowLayout = new LinearLayout(this);
                            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                            for (int j = i * 3; j < (i + 1) * 3; j++) {
                                addImgView(j, imgs, j != i * 3, rowLayout, targetW);
                            }
                            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mTargetW,
                                    getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                            rowParam.setMargins(0, getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0);
                            mImgLayout.addView(rowLayout, rowParam);
                        }
                    } else {//多加一行
                        rows = rows + 1;
                        for (int i = 0; i < rows; i++) {
                            LinearLayout rowLayout = new LinearLayout(this);
                            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                            if (i == rows - 1) {
                                for (int j = i * 3; j < (rows - 1) * 3 + size % 3; j++) {
                                    addImgView(j, imgs, j != i * 3, rowLayout, targetW);
                                }
                            } else {
                                for (int j = i * 3; j < (i + 1) * 3; j++) {
                                    addImgView(j, imgs, j != i * 3, rowLayout, targetW);
                                }
                            }
                            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(mTargetW,
                                    getResources().getDimensionPixelSize(R.dimen.post_list_three_pic));
                            rowParam.setMargins(0, getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0);
                            mImgLayout.addView(rowLayout, rowParam);
                        }
                    }
                }
            }
        } else {
            mImgLayout.setVisibility(View.GONE);
        }

    }

    private void addImgView(final int index, final List<Img> imgs, boolean b, LinearLayout rowLayout, int targetSize) {
        ImageView imageView = new ImageView(this);
        x.image().bind(imageView, packageUrl(targetSize, imgs.get(index).getUrl()));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(targetSize, targetSize);
        if (b) {
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.pic_margin), 0, 0, 0);
        }
        rowLayout.addView(imageView, params);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPreviewPage(index, imgs);
            }
        });
    }

    private void toPreviewPage(int position, List<Img> imgs) {
        ArrayList<UserPhotoBean> photos = new ArrayList<UserPhotoBean>();
        for (Img img : imgs) {
            UserPhotoBean bean = new UserPhotoBean();
            bean.setPhotoOrigin(img.getUrl());
            photos.add(bean);
        }
        Intent intent = new Intent(this, UserPhotoScanActivity.class);
        intent.putExtra("photolist", photos);
        intent.putExtra("photoindex", position);
        intent.putExtra("isMyself", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String packageUrl(int dimensionPixelSize, String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("@").append(dimensionPixelSize + "w").append("_1l");
        return sb.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0 && position != 1) {
            Comment comment = mData.get(position - mListview.getHeaderViewsCount());
            if (comment != null) {
                mCommentEt.setHint(getResources().getString(R.string.reply_who, comment.getUserFromName()));
                mReplayComment = comment;
                mCommentEt.setFocusable(true);
                mCommentEt.setFocusableInTouchMode(true);
                mCommentEt.requestFocus();
                CommonUtils.showKeyboard(PostsDetailActivity.this, mCommentEt);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.praise_imageview:
                if (mPosts.getIsPraise() == 1) {
                    setPraiseUi(0);
                } else {
                    setPraiseUi(1);
                }
                praise();
                break;
            case R.id.comment_imageview:
                if (!mKeyboardShow) {
                    mCommentEt.setFocusable(true);
                    mCommentEt.setFocusableInTouchMode(true);
                    mCommentEt.requestFocus();
                    CommonUtils.showKeyboard(this, mCommentEt);
                    mKeyboardShow = true;
                } else {
                    CommonUtils.hideKeyboard(this);
                    mKeyboardShow = false;
                }
                break;
            case R.id.more_layout:
                showOperation(mUserCode == mPosts.getUserCode());
                break;
            case R.id.head_imageview:
                Intent intent = new Intent(this, MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(mPosts.getUserCode()));
                startActivity(intent);
                break;
            case R.id.nickname_textview:
                mHeaderImg.performClick();
                break;
        }
    }

    /**
     * 举报或删除
     */
    private void showOperation(final boolean b) {
        final AlertDialog portraidlg = new AlertDialog.Builder(this).create();
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
            ((TextView) jubaoLayout.findViewById(R.id.operation_textview)).setText(getResources().getString(R.string.delete));
        }
        jubaoLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String tips, title;
                if (b) {
                    title = getResources().getString(R.string.delete_title);
                    tips = getResources().getString(R.string.delete_tips);
                } else {
                    title = getResources().getString(R.string.report_title);
                    tips = getResources().getString(R.string.report_tips);
                }
                portraidlg.dismiss();
                MyHintDialog.getDialog(PostsDetailActivity.this, title, tips, getResources().getString(R.string.sure),
                        new MyHintDialog.OnDialogClick() {

                            @Override
                            public void onOK() {
                                if (b) {
                                    deletePost();
                                } else {
                                    reportPost();
                                }
                            }

                            @Override
                            public void onDismiss() {

                            }
                        });
            }
        });
    }

    private void deletePost() {
        RequestParams params = new RequestParams(HttpContants.POST_DELETEPOST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", mPostsId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsDetailActivity.this)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(PostsDetailActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(PostsDetailActivity.this, getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra(Constant.POSTS, mPosts);
                setResult(Constant.COMMENT_RESULT_CODE, i);
                PostsDetailActivity.this.finish();
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

    private void reportPost() {
        RequestParams params = new RequestParams(HttpContants.REPORT_CREATEREPORT);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", mPostsId);
            requestObject.put("commentId", 0);
            requestObject.put("userCode", mPosts.getUserCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsDetailActivity.this)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(PostsDetailActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(PostsDetailActivity.this, getResources().getString(R.string.report_success), Toast.LENGTH_SHORT).show();
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

    private void praise() {
        RequestParams params = new RequestParams(HttpContants.PRAISE_DOPRAISE);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", mPostsId);
            requestObject.put("userCode", mPosts.getUserCode());
            if (mPosts.getIsPraise() == 1) {
                requestObject.put("praise", 0);
            } else if (mPosts.getIsPraise() == 0) {
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
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsDetailActivity.this)) {
                    return;
                }
                PraiseData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(PostsDetailActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                if (mPosts.getIsPraise() == 1) {
                    mPosts.setIsPraise(0);
                } else if (mPosts.getIsPraise() == 0) {
                    mPosts.setIsPraise(1);
                }
                mPosts.setPraiseNum(data.getPraiseNum());
                mPraiseTv.setText(getResources().getString(R.string.praise_num, mPosts.getPraiseNum()));
                Intent i = new Intent();
                i.putExtra(Constant.POSTS, mPosts);
                setResult(Constant.PRAISE_RESULT_CODE, i);
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getPostsDetail(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(this, getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
        }
        if (mData.size() > 0) {
            getPostsDetail(mData.get(mData.size() - 1).getCreateAt());
        } else {
            getPostsDetail(0);
        }
    }

    @Override
    public void whenDelete(Comment comment) {
        if (mData.contains(comment)) {
            mData.remove(comment);
            mCommentAdapter.notifyDataSetChanged();
            mPosts.setCommentNum(mPosts.getCommentNum() - 1);
            mCommentTv.setText(getResources().getString(R.string.comment_num, mPosts.getCommentNum()));
            Intent i = new Intent();
            i.putExtra(Constant.POSTS, mPosts);
            setResult(Constant.COMMENT_RESULT_CODE, i);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        getPostsDetail(0);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(this, getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
        }
        if (mData.size() > 0) {
            getPostsDetail(mData.get(mData.size() - 1).getCreateAt());
        } else {
            getPostsDetail(0);
        }
        return true;
    }
}
