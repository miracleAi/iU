package com.android.biubiu.ui.discovery.homepage;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.activity.act.WebviewActivity;
import com.android.biubiu.transport.http.response.community.Banner;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.transport.http.response.community.DiscoveryData;
import com.android.biubiu.transport.http.response.community.Posts;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.viewflipper.ViewFlipperForListview;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.NetUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.imeetu.iu.R;

/**
 * 帖子列表页面,分推荐、新鲜、biubiu
 */
public class PostsFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener, PostsAdapter.IRefreshUi {
    private final String TAG = PostsFragment.class.getSimpleName();
    private static final int TO_DETAIL_PAGE = 0;
    private int mType;//0--新鲜，1--推荐，2--biubiu

    private int mHasNext;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;
    private PostsAdapter mAdapter;

    private List<Posts> mData = new ArrayList<Posts>();
    private View mHeadAdBanner;
    private ViewFlipperForListview mFlipper;
    private LinearLayout mIndicatorLayout;

    //判断滑动手势
    private GestureDetector mDetector;
    private ScheduledExecutorService mService;
    private ScheduledFuture<?> mFuture;
    private Handler mHandler = new Handler();
    private boolean mIsSet, mAddHeadBanner;
    private boolean mScrollFlag;
    private int mLastVisibleItemPosition;
    private ITabPageIndicatorAnim mTabPageIndicatorAnim;
    private int mBannerH;
    private ImageOptions mImgOptions;
    private long mNextStart;
    private String mDataNullTips;
    private int sexFlag;

    public PostsFragment() {
    }

    public interface ITabPageIndicatorAnim {
        void slideUp();

        void slideDown();
    }

    public void setTabPageIndicatorAnim(ITabPageIndicatorAnim tabPageIndicatorAnim) {
        mTabPageIndicatorAnim = tabPageIndicatorAnim;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_posts, container, false);
        mHeadAdBanner = inflater.inflate(R.layout.home_header_adbanner, null);
        initView();
        initData();
        return mRootview;
    }
    private void initView() {
        mPullToRefreshListview = (PullToRefreshListView) mRootview.findViewById(R.id.pull_refresh_list);
        sexFlag = SharePreferanceUtils.getInstance().getUserSex(getActivity(),SharePreferanceUtils.USER_SEX,0);
        mPullToRefreshListview.setSexFlag(getActivity(),sexFlag);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();

        mFlipper = (ViewFlipperForListview) mHeadAdBanner.findViewById(R.id.banner_flipper);
        mIndicatorLayout = (LinearLayout) mHeadAdBanner.findViewById(R.id.indicator_layout);

        /*mListview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                        mScrollFlag = false;
                        // 判断滚动到底部
                        if (mListview.getLastVisiblePosition() == (mListview.getCount() - 1)) {

                        }
                        // 判断滚动到顶部
                        if (mListview.getFirstVisiblePosition() == 0) {

                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        mScrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                        mScrollFlag = false;
                        break;
                }
            }

            *//**
         * firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
         * visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
         *//*
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
                if (mScrollFlag*//* && ScreenUtil.getScreenViewBottomHeight(mListview) >= ScreenUtil.getScreenHeight(getActivity())*//*) {
                    if (firstVisibleItem > mLastVisibleItemPosition) {// 上滑
                        if (mTabPageIndicatorAnim != null) {
                            mTabPageIndicatorAnim.slideUp();
                        }
                    } else if (firstVisibleItem < mLastVisibleItemPosition) {// 下滑
                        if (mTabPageIndicatorAnim != null) {
                            mTabPageIndicatorAnim.slideDown();
                        }
                    } else {
                        return;
                    }
                    mLastVisibleItemPosition = firstVisibleItem;
                }
            }
        });*/
    }

    private void initData() {
        if (mType == 0) {
            mDataNullTips = getResources().getString(R.string.posts_fresh_null);
        } else if (mType == 1) {
            mDataNullTips = getResources().getString(R.string.posts_recommand_null);
        } else {
            mDataNullTips = getResources().getString(R.string.posts_biu_null);
        }
        mImgOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.banner_fail)
                .setLoadingDrawableId(R.drawable.banner_fail)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
        mBannerH = getResources().getDimensionPixelSize(R.dimen.ad_banner_height);
        mListview.setOnItemClickListener(this);

        mType = getArguments().getInt("type", 0);
        getData(0);
        mService = Executors.newSingleThreadScheduledExecutor();
        mDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {

            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (mFlipper.getChildCount() > 1) {
                    if (e1.getX() - e2.getX() > 100) {//左滑
                        mFuture.cancel(true);
                        slideToLeft();
                        return true;
                    } else if (e1.getX() - e2.getX() < -100) {//右滑
                        mFuture.cancel(true);
                        slideToRight();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                ImageView iv = (ImageView) mFlipper.getCurrentView();
                Banner ad = (Banner) iv.getTag();
                Intent i = new Intent(getActivity(), WebviewActivity.class);
                i.putExtra(Constant.ACTIVITY_NAME, ad.getTitle());
                i.putExtra(Constant.ACTIVITY_COVER, ad.getCover());
                i.putExtra(Constant.ACTIVITY_URL, ad.getUrl());
                startActivity(i);
                return false;
            }
        });
        mFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

    private void getData(final long time) {
        if (time == 0 && mData.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(getActivity())) {
            dismissLoadingLayout();
            if (time == 0 && mData.size() == 0) {
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getData(mNextStart);
                    }
                });
            }
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        RequestParams params = new RequestParams(HttpContants.POST_GETPOSTLISTBYTYPE);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("type", mType);
            requestObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                stopLoad();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                stopLoad();
                if (time == 0 && mData.size() == 0) {
                    dismissLoadingLayout();
                    showErrorLayout(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dismissErrorLayout();
                            getData(mNextStart);
                        }
                    });
                } else {
                    toastShort(getResources().getString(R.string.pull_up_failed));
                }
            }

            @Override
            public void onFinished() {
                stopLoad();
            }

            @Override
            public void onSuccess(String result) {
                stopLoad();
                dismissLoadingLayout();
                LogUtil.d(TAG, "getPostlist-- mType = " + mType + result);
                Data<DiscoveryData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<DiscoveryData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), getActivity())) {
                    return;
                }
                DiscoveryData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHasNext();
                List<Posts> postsList = data.getPostList();
                if (time == 0) {//第一次请求或下拉刷新
                    //实例化banner
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                    if (mAdapter == null) {
                        mAdapter = new PostsAdapter(mData, getActivity());
                        mAdapter.setIRefreshUi(PostsFragment.this);
                    }
                    List<Banner> banners = data.getBanner();
                    if (banners != null && banners.size() > 0) {
                        if (!mAddHeadBanner) {
                            mListview.addHeaderView(mHeadAdBanner);
                            mAddHeadBanner = true;
                        } else {
                            mListview.removeHeaderView(mHeadAdBanner);
                            mListview.addHeaderView(mHeadAdBanner);
                        }
                        initBanners(banners);
                    } else {
                        mListview.removeHeaderView(mHeadAdBanner);
                    }
                    if (!mIsSet) {
                        mListview.setAdapter(mAdapter);
                        mIsSet = true;
                    }
                }
                if (postsList != null && postsList.size() > 0) {
                    mData.addAll(postsList);
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (time == 0) {
                        showDataEmpty(null, mDataNullTips);
                    }
                }
            }
        });
    }

    private void initBanners(List<Banner> banners) {
        if (mIndicatorLayout.getChildCount() > 0) {
            mIndicatorLayout.removeAllViews();
        }
        if (mFlipper.getChildCount() > 0) {
            mFlipper.removeAllViews();
        }
        int size = banners.size();
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        if (size > 1) {
            mFuture = mService.scheduleAtFixedRate(new Task(), 1, Constant.BANNER_ANIM_INTERVAL, TimeUnit.SECONDS);
        }
        for (int i = 0; i < size; i++) {
            final ImageView bannerImageView = new ImageView(getActivity());
            final Banner banner = banners.get(i);
            /*final int finalI = i;
            x.image().bind(bannerImageView, packageUrl(banner.getCover()), mImgOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    System.out.println("i = " + finalI + " drawable = " + drawable);
                    bannerImageView.setImageDrawable(drawable);
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    System.out.println("i = " + finalI + " onError");
                }

                @Override
                public void onCancelled(CancelledException e) {
                    System.out.println("i = " + finalI + " onCancelled");
                }

                @Override
                public void onFinished() {
                    System.out.println("i = " + finalI + " onFinished");
                }
            });*/
//            x.image().bind(bannerImageView,banner.getCover());
            Picasso.with(getActivity()) //
                    .load(/*packageUrl(*/banner.getCover()/*)*/) //
                    .placeholder(R.drawable.banner_fail) //
                    .error(R.drawable.banner_fail) //
//                    .fit() //
                    .tag(PostsFragment.this) //
                    .into(bannerImageView);
            bannerImageView.setTag(banner);
            bannerImageView.setId(i);
            bannerImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup.LayoutParams imgParams = new ViewGroup.LayoutParams(Constant.screenWidth, mBannerH);
            mFlipper.addView(bannerImageView, i, imgParams);
            if (size > 1) {
                View v = new View(getActivity());
                v.setId(i);
                v.setBackgroundResource(R.drawable.indicator_default);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.indicator_width),
                        getResources().getDimensionPixelSize(R.dimen.indicator_height));
                if (i != 0) {
                    params.setMargins(getResources().getDimensionPixelSize(R.dimen.indicator_line_margin), 0, 0, 0);
                }
                mIndicatorLayout.addView(v, i, params);
            }
        }
    }

    private String packageUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("@").append(mBannerH + "h_" + Constant.screenWidth + "w_0e");
        return sb.toString();
    }

    private void stopLoad() {
        if (mPullToRefreshListview.isRefreshing()) {
            mPullToRefreshListview.onRefreshComplete();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
        }
        mNextStart = mData.get(mData.size() - 1).getCreateAt();
        getData(mData.get(mData.size() - 1).getCreateAt());
    }

    private void slideToLeft() {// 向左动画方法
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
        mFlipper.showNext();
        mFlipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (mFlipper.getChildCount() > 1) {
                    if (mFuture.isCancelled()) {
                        mFuture = mService.scheduleAtFixedRate(new Task(), 1, Constant.BANNER_ANIM_INTERVAL, TimeUnit.SECONDS);
                    }
                }
//                mBannerName.setText(((Banner) mFlipper.getCurrentView().getTag()).getTitle());
                setIndicatorLine(mFlipper.getDisplayedChild());
            }
        });

    }

    private void slideToRight() {//向右滑动方法
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
        mFlipper.showPrevious();
        mFlipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (mFlipper.getChildCount() > 1) {
                    if (mFuture.isCancelled()) {
                        mFuture = mService.scheduleAtFixedRate(new Task(), 1, Constant.BANNER_ANIM_INTERVAL, TimeUnit.SECONDS);
                    }
                }
//                mBannerName.setText(((Banner) mFlipper.getCurrentView().getTag()).getTitle());
                setIndicatorLine(mFlipper.getDisplayedChild());
            }
        });
        // 当手势划过后重设自动播放的方向
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
    }

    private void setIndicatorLine(int id) {
        for (int i = 0, size = mFlipper.getChildCount(); i < size; i++) {
            if (i == id) {
                mIndicatorLayout.getChildAt(i).setBackgroundResource(R.drawable.indicator_selected);
            } else {
                mIndicatorLayout.getChildAt(i).setBackgroundResource(R.drawable.indicator_default);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(LoginUtils.isLogin(getActivity())){
            Posts posts = mData.get(position - mListview.getHeaderViewsCount());
            if (posts != null) {
                Intent i = new Intent(getActivity(), PostsDetailActivity.class);
                i.putExtra(Constant.POSTS, posts);
                startActivityForResult(i, TO_DETAIL_PAGE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_DETAIL_PAGE) {
            if (resultCode == Constant.DELETE_RESULT_CODE) {
                Posts posts = (Posts) data.getSerializableExtra(Constant.POSTS);
                refreshListWhenDelete(posts);
            } else if (resultCode == Constant.PRAISE_RESULT_CODE) {
                Posts posts = (Posts) data.getSerializableExtra(Constant.POSTS);
                refreshListWhenPraise(posts);
            } else if (resultCode == Constant.COMMENT_RESULT_CODE) {
                Posts posts = (Posts) data.getSerializableExtra(Constant.POSTS);
                refreshListWhenPraise(posts);
            }
        }
    }

    private void refreshListWhenDelete(Posts posts) {
        for (int i = mData.size() - 1; i > -1; i--) {
            if (mData.get(i).getPostId() == posts.getPostId()) {
                mData.remove(mData.get(i));
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void refreshListWhenPraise(Posts posts) {
        for (Posts p : mData) {
            if (p.getPostId() == posts.getPostId()) {
                p.setIsPraise(posts.getIsPraise());
                p.setPraiseNum(posts.getPraiseNum());
                p.setCommentNum(posts.getCommentNum());
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void whenDelete(Posts posts) {
        if (mData.contains(posts)) {
            mData.remove(posts);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 默认滑动从右向左滑动
     *
     * @author Administrator
     */
    public class Task implements Runnable {
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    slideToLeft();
                }
            });
        }
    }

    public void refreshList() {
        onPullDownToRefresh(mPullToRefreshListview);
        mListview.setSelection(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFuture != null && mFuture.isCancelled()) {
            mFuture = mService.scheduleAtFixedRate(new Task(), 1, Constant.BANNER_ANIM_INTERVAL, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFuture != null) {
            mFuture.cancel(true);
        }
    }

    public void refreshData() {
        if(getActivity()!=null){
            if (mData.size() == 0) {
                getData(0);
            }
        }
    }
}
