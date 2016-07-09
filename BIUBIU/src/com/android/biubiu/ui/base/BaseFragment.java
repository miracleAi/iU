package com.android.biubiu.ui.base;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.component.util.DensityUtil;
import com.ant.liao.GifView;
import com.umeng.analytics.MobclickAgent;

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

    public void showDataEmpty(View.OnClickListener listener, String empty) {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) mRootview.findViewById(R.id.error_layout);
        }
        if (reloadLayout == null) {
            reloadLayout = (LinearLayout) mRootview.findViewById(R.id.reloading_layout);
        }
        TextView emptyTv = (TextView) reloadLayout.findViewById(R.id.reload_tips_textview);
        emptyTv.setText(empty);
        reloadLayout.findViewById(R.id.error_imageview).setVisibility(View.GONE);
        reloadLayout.setOnClickListener(listener);
        errorLayout.setVisibility(View.VISIBLE);
    }

    public void dismissDataEmpty() {
        if (errorLayout == null) {
            errorLayout = (LinearLayout) mRootview.findViewById(R.id.error_layout);
        }
        TextView emptyTv = (TextView) mRootview.findViewById(R.id.reload_tips_textview);
        emptyTv.setText(getActivity().getResources().getString(R.string.reload_tips));
        reloadLayout.findViewById(R.id.error_imageview).setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }
}
