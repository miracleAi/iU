package com.android.biubiu.ui.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.android.biubiu.application.BiubiuApplication;
import com.android.biubiu.component.title.TopTitleView;

import java.lang.ref.WeakReference;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BasicFragment extends BaseFragment {

    protected WeakReference<BaseFragment> mWeakFragment;
    protected WeakReference<BaseActivity> mWeakActivity;
//    protected FragmentHandler mHandler = new FragmentHandler(this);
    protected View mRootView;
    protected TopTitleView mTopTitleView;
    protected BiubiuApplication mApp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWeakActivity = new WeakReference<>((BaseActivity) context);
        this.mWeakFragment = new WeakReference<BaseFragment>(this);
        mApp = (BiubiuApplication) ((Activity) context).getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //解决fragment叠加时透点的问题
        mRootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (mRootView.findViewById(R.id.top_title_view) != null) {
            mTopTitleView = (TopTitleView) mRootView.findViewById(R.id.top_title_view);
            mTopTitleView.setLeftOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = mWeakActivity.get().getSupportFragmentManager();
                    if (manager.getBackStackEntryCount() > 1) {
                        manager.popBackStack();
                    } else {
                        mWeakActivity.get().finish();
                    }
                }
            });
        }
        initView();
        initData();
    }

    protected void popFragment() {
        mWeakActivity.get().getSupportFragmentManager().popBackStack();
    }

    @SuppressLint("NewApi")
    protected boolean checkUIThread() {
        if (mWeakActivity.get() != null && !mWeakActivity.get().isFinishing() /*&& !mWeakActivity.get().isDestroyed()*/
                && mWeakFragment.get() != null && mWeakFragment.get().isAdded()) {
            return true;
        } else {
            return false;
        }
    }

    protected final FragmentTransaction setTransactionAnim(FragmentTransaction ft) {
//        ft.setCustomAnimations(R.anim.in_from_right, R.anim.alpha_out, R.anim.activity_show, R.anim.out_to_left);
        return ft;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    protected final void startActivityFromFragment(Intent intent) {
        this.startActivity(intent);
//        mWeakActivity.get().overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
    }

    protected final void startActivityFromFragmentForResult(Intent intent, int requestCode) {
        this.startActivityForResult(intent, requestCode);
//        mWeakActivity.get().overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
    }

    /*protected void initHandler() {
        mHandler = new FragmentHandler(this);
    }

    protected void handleMessage(Message msg) {

    }

    protected static class FragmentHandler extends Handler {
        private final WeakReference<BaseFragment> mOutterFragment;

        public FragmentHandler(BaseFragment fragment) {
            mOutterFragment = new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mOutterFragment.get() != null && mOutterFragment.get().isAdded()) {
                mOutterFragment.get().handleMessage(msg);
                return;
            }
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("notUsed")
    protected void openFragment(Fragment fragment, String tag) {
        FragmentManager fm = ((BaseActivity) mWeakActivity.get()).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        setTransactionAnim(ft);
        ft.add(R.id.layout_body, fragment, tag).addToBackStack(null).commit();
    }

    @SuppressWarnings("notUsed")
    protected void clearFragment() {
        FragmentManager manager = mWeakActivity.get().getSupportFragmentManager();
        for (int i = 2; i < manager.getBackStackEntryCount(); i++) {
            manager.popBackStack();
        }
    }


}
