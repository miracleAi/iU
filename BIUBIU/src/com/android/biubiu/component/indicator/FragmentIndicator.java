package com.android.biubiu.component.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.imeetu.iu.R;

/**
 * @author yhj-pc
 */
public class FragmentIndicator extends LinearLayout implements OnClickListener {
    private int mPreViewId;
    private OnIndicateListener mOnIndicateListener;
    private static final int COLOR_UNSELECT = Color.argb(255, 0xcc, 0xcc, 0xcc);
    private static final int COLOR_SELECT = Color.argb(255, 0xff, 0xff, 0x8f);

    private Context mCon;
    private Indicator mCurrIndicator;

    private FragmentIndicator(Context context) {
        super(context);
    }

    public FragmentIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        mCon = context;

    }

    public FragmentIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDefaultView(int id) {
        mPreViewId = id;
    }

    public FragmentIndicator addChild(Indicator indicator) {
        View view = LayoutInflater.from(mCon).inflate(R.layout.indicator_layout, null);
        initialize(view, indicator);
        return this;
    }

    private void initialize(View view, Indicator indicator) {
        view.setId(indicator.getId());
        view.setTag(indicator);
        if (/*indicator.getFragment() == null || */indicator.getTitle() == 0) {
            view.findViewById(R.id.pic_word_layout).setVisibility(GONE);
            view.findViewById(R.id.extra_img).setVisibility(VISIBLE);
            ((ImageView) view.findViewById(R.id.extra_img)).setImageResource(indicator.getDefaultIcon());
        } else {
            ((ImageView) view.findViewById(R.id.ivIcon)).setImageResource(indicator.getDefaultIcon());
            ((TextView) view.findViewById(R.id.tvText)).setText(mCon.getResources().getString(indicator.getTitle()));
        }
        view.setOnClickListener(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        addView(view, params);
    }

    public void setUnReadVisible(int id, boolean visible) {
        View v = findViewById(id);
        ImageView iv = (ImageView) v.findViewById(R.id.unread_image);
        if (visible) {
            iv.setVisibility(VISIBLE);
        } else {
            iv.setVisibility(GONE);
        }
    }


    public void setIndicator(int viewId) {
        Indicator indicator = (Indicator) findViewById(viewId).getTag();
        if (indicator.getFragment() != null) {
            if (mPreViewId != 0) {
                Indicator preview = (Indicator) findViewById(mPreViewId).getTag();
                showPreview(preview);
            }
            showCurrent(viewId, indicator);
            mPreViewId = viewId;
        }
    }

    private void showCurrent(int viewId, Indicator indicator) {
        ImageView currIcon;
        TextView currText;
        View view = findViewById(viewId);
        if (indicator.getTitle() == 0) {
            currIcon = (ImageView) view.findViewById(R.id.extra_img);
        } else {
            currIcon = (ImageView) view.findViewById(R.id.ivIcon);
            currText = (TextView) view.findViewById(R.id.tvText);
            currText.setTextColor(COLOR_SELECT);
        }
        currIcon.setImageResource(indicator.getSelectedIcon());
        mCurrIndicator = indicator;
    }

    private void showPreview(Indicator indicator) {
        ImageView prevIcon;
        TextView prevText;
        View curView = findViewById(mPreViewId);
        if (indicator.getTitle() == 0) {
            prevIcon = (ImageView) curView.findViewById(R.id.extra_img);
        } else {
            prevIcon = (ImageView) curView.findViewById(R.id.ivIcon);
            prevText = (TextView) curView.findViewById(R.id.tvText);
            prevText.setTextColor(COLOR_UNSELECT);
        }
        prevIcon.setImageResource(indicator.getDefaultIcon());

    }

    public interface OnIndicateListener {
        void onIndicate(int id);
    }

    public void setOnIndicateListener(OnIndicateListener listener) {
        mOnIndicateListener = listener;
    }

    @Override
    public void onClick(View v) {
        mOnIndicateListener.onIndicate(v.getId());
        setIndicator(v.getId());
        /*if (mCurrIndicator != null && mCurrIndicator.getClickTime() > 1) {
            BaseFragment fragment = mCurrIndicator.getFragment();
            if (fragment instanceof Indicator.IRefreshPageListener) {
                Indicator.IRefreshPageListener refreshPageListener = (Indicator.IRefreshPageListener) fragment;
                refreshPageListener.refreshPage();
            }
        }*/
    }
}
