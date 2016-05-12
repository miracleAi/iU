package com.android.biubiu.activity.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WebviewActivityFragment extends Fragment {
    private View mRootview;
    private WebView mWebview;
    private TopTitleView mToptitle;
    private String mAdName, mAdCover, mAdUrl;

    public WebviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebview = (WebView) mRootview.findViewById(R.id.webview);
        mToptitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mToptitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Bundle b = getArguments();
        if (b != null) {
            mAdName = b.getString(Constant.ACTIVITY_NAME);
            mAdCover = b.getString(Constant.ACTIVITY_COVER);
            mAdUrl = b.getString(Constant.ACTIVITY_URL);

            mToptitle.setTitle(mAdName);

            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setUseWideViewPort(true);
            mWebview.getSettings().setLoadWithOverviewMode(true);
            mWebview.loadUrl(mAdUrl);
            mWebview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);// 使用当前WebView处理跳转
                    return true;// true表示此事件在此处被处理，不需要再广播
                }

                @Override
                // 转向错误时的处理
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                }
            });

        }
        return mRootview;
    }
}
