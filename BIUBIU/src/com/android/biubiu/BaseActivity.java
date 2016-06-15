package com.android.biubiu;

import cc.imeetu.iu.R;

import com.android.biubiu.myapplication.BiubiuApplication;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
    private LinearLayout loadingLayout;
    private GifView loadGif;
    private TextView loadTv;
    private LinearLayout errorLayout;
    private LinearLayout reloadLayout;
    private BiubiuApplication biuApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, true);
        biuApplication = (BiubiuApplication)this.getApplication();
        biuApplication.addAppInstance(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        biuApplication.delAppInstanceByAnim(this);
        super.onDestroy();

    }

    /**
     * 显示Toast长通知
     *
     * @param msg
     */
    public void toastLong(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示Toast短通知
     *
     * @param msg
     */
    public void toastShort(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //显示加载中
    public void showLoadingLayout(String loadingStr) {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        }
        if (loadGif == null) {
            loadGif = (GifView) findViewById(R.id.load_gif);
            loadGif.setGifImage(R.drawable.loadingbbbb);
            loadGif.setShowDimension(DensityUtil.dip2px(getApplicationContext(), 30), DensityUtil.dip2px(getApplicationContext(), 30));
            loadGif.setGifImageType(GifImageType.COVER);
        }
        if (loadTv == null) {
            loadTv = (TextView) findViewById(R.id.loading_tv);
        }
        loadTv.setText(loadingStr);
        loadGif.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    //加载完毕隐藏
    public void dismissLoadingLayout() {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        }
        loadingLayout.setVisibility(View.GONE);
    }

    //显示错误界面
    public void showErrorLayout(OnClickListener listener) {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        }
        if (reloadLayout == null) {
            reloadLayout = (LinearLayout) findViewById(R.id.reloading_layout);
        }
        reloadLayout.setOnClickListener(listener);
        errorLayout.setVisibility(View.VISIBLE);
    }

    //显示错误界面
    public void dismissErrorLayout() {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        }
        errorLayout.setVisibility(View.GONE);
    }

    public void showDataEmpty(View.OnClickListener listener, String empty) {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        }
        if (reloadLayout == null) {
            reloadLayout = (LinearLayout) findViewById(R.id.reloading_layout);
        }
        TextView emptyTv = (TextView) reloadLayout.findViewById(R.id.reload_tips_textview);
        emptyTv.setText(empty);
        reloadLayout.findViewById(R.id.error_imageview).setVisibility(View.GONE);
        reloadLayout.setOnClickListener(listener);
        errorLayout.setVisibility(View.VISIBLE);
    }

    public void dismissDataEmpty() {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        }
        TextView emptyTv = (TextView) findViewById(R.id.reload_tips_textview);
        emptyTv.setText(getResources().getString(R.string.reload_tips));
        errorLayout.setVisibility(View.GONE);
    }
}
