package com.android.biubiu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.utils.DensityUtil;
import com.ant.liao.GifView;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    protected View mRootview;
    private LinearLayout loadingLayout;
    private GifView loadGif;
    private TextView loadTv;
    private LinearLayout errorLayout;
    private LinearLayout reloadLayout;

    public BaseFragment() {
    }

    /**
     * 显示Toast短通知
     *
     * @param msg
     */
    public void toastShort(CharSequence msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    //显示加载中
    public void showLoadingLayout(String loadingStr) {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) mRootview.findViewById(R.id.loading_layout);
        }
        if (loadGif == null) {
            loadGif = (GifView) mRootview.findViewById(R.id.load_gif);
            loadGif.setGifImage(R.drawable.loadingbbbb);
            loadGif.setShowDimension(DensityUtil.dip2px(getActivity(), 30), DensityUtil.dip2px(getActivity(), 30));
            loadGif.setGifImageType(GifView.GifImageType.COVER);
        }
        if (loadTv == null) {
            loadTv = (TextView) mRootview.findViewById(R.id.loading_tv);
        }
        loadTv.setText(loadingStr);
        loadGif.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    //加载完毕隐藏
    public void dismissLoadingLayout() {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) mRootview.findViewById(R.id.loading_layout);
        }
        loadingLayout.setVisibility(View.GONE);
    }

    //显示错误界面
    public void showErrorLayout(View.OnClickListener listener) {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) mRootview.findViewById(R.id.error_layout);
        }
        if (reloadLayout == null) {
            reloadLayout = (LinearLayout) mRootview.findViewById(R.id.reloading_layout);
        }
        reloadLayout.setOnClickListener(listener);
        errorLayout.setVisibility(View.VISIBLE);
    }

    //显示错误界面
    public void dismissErrorLayout() {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) mRootview.findViewById(R.id.error_layout);
        }
        errorLayout.setVisibility(View.GONE);
    }

}
