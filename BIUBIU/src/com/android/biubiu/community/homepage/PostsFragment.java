package com.android.biubiu.community.homepage;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.android.biubiu.bean.community.Banner;
import com.android.biubiu.bean.base.Data;
import com.android.biubiu.bean.community.DiscoveryData;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.viewflipper.ViewFlipperForListview;
import com.android.biubiu.utils.CommonUtils;
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
import java.util.concurrent.ExecutorService;
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

    private static final int INDICATOR_COLOR = Color.argb(102, 0xFF, 0xFF, 0xFF);
    private static final int CUR_INDICATOR_COLOR = Color.argb(255, 0xFF, 0xFF, 0xFF);
    //判断滑动手势
    private GestureDetector mDetector;
    private ScheduledExecutorService mService;
    ExecutorService mThreadPool;
    private ScheduledFuture<?> mFuture;
    private Handler mHandler = new Handler();
    private boolean mIsSet, mAddHeadBanner;

    public PostsFragment() {
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
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();

        mFlipper = (ViewFlipperForListview) mHeadAdBanner.findViewById(R.id.banner_flipper);
        mIndicatorLayout = (LinearLayout) mHeadAdBanner.findViewById(R.id.indicator_layout);
    }

    private void initData() {
        mListview.setOnItemClickListener(this);

        mType = getArguments().getInt("type", 0);
        getData(0);
        mThreadPool = Executors.newFixedThreadPool(5);
        mService = Executors.newSingleThreadScheduledExecutor();
        mFuture = mService.scheduleAtFixedRate(new Task(), 1, 5, TimeUnit.SECONDS);
        mDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {

            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 100) {//左滑
                    mFuture.cancel(true);
                    slideToLeft();
                    return true;
                } else if (e1.getX() - e2.getX() < -100) {//右滑
                    mFuture.cancel(true);
                    slideToRight();
                    return true;
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
            }

            @Override
            public void onFinished() {
                stopLoad();
            }

            @Override
            public void onSuccess(String result) {
                stopLoad();
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
        for (int i = 0; i < size; i++) {
            ImageView bannerImageView = new ImageView(getActivity());
            Banner banner = banners.get(i);
            x.image().bind(bannerImageView, banner.getCover());
            bannerImageView.setTag(banner);
            bannerImageView.setId(i);
            bannerImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mFlipper.addView(bannerImageView, i);

            View v = new View(getActivity());
            v.setId(i);
            v.setBackgroundColor(INDICATOR_COLOR);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.indicator_width),
                    getResources().getDimensionPixelSize(R.dimen.indicator_height));
            if (i != 0) {
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.indicator_line_margin), 0, 0, 0);
            }
            mIndicatorLayout.addView(v, i, params);
        }
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
            stopLoad();
        } else {
            getData(mData.get(mData.size() - 1).getCreateAt());
        }
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
                if (mFuture.isCancelled()) {
                    mFuture = mService.scheduleAtFixedRate(new Task(), 1, 5, TimeUnit.SECONDS);
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
                if (mFuture.isCancelled()) {
                    mFuture = mService.scheduleAtFixedRate(new Task(), 1, 5, TimeUnit.SECONDS);
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
                mIndicatorLayout.getChildAt(i).setBackgroundColor(CUR_INDICATOR_COLOR);
            } else {
                mIndicatorLayout.getChildAt(i).setBackgroundColor(INDICATOR_COLOR);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Posts posts = mData.get(position - mListview.getHeaderViewsCount());
        if (posts != null) {
            Intent i = new Intent(getActivity(), PostsDetailActivity.class);
            i.putExtra(Constant.POSTS, posts);
            startActivityForResult(i, TO_DETAIL_PAGE);
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

    public void refreshList(){
        onPullDownToRefresh(mPullToRefreshListview);
        mListview.setSelection(0);
    }
}
