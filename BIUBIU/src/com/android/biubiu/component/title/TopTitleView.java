package com.android.biubiu.component.title;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.biubiu.component.util.DensityUtil;

import cc.imeetu.iu.R;


/**
 * 标题栏控件
 *
 * @author yanghj
 */
public class TopTitleView extends LinearLayout {

    private TextView mTvTitle;
    private ImageView mImageViewCenter;
    private ImageView mImgLeft, mUnReadImg, mLeftUnReadImg, mRightUnreadImg;
    private TextView mTvLeft;
    private ImageView mImgRight;
    private TextView mTvRight;
    private ImageView mImgCenter;
    private TextView mTvCenter;
    private ViewGroup mLeftLayout;
    private ViewGroup mRightLayout;
    private ViewGroup mCenterLayout;
    private ViewGroup mMiddleLayout;
    private View mDivideLine;

    public TopTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, 0);
    }

    public TopTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View top = LayoutInflater.from(context).inflate(R.layout.top_title_layout, this, false);
        mLeftLayout = (ViewGroup) top.findViewById(R.id.top_title_left_layout);
        mRightLayout = (ViewGroup) top.findViewById(R.id.top_title_right_layout);
        mCenterLayout = (ViewGroup) top.findViewById(R.id.top_title_center_icon_layout);
        mMiddleLayout = (ViewGroup) top.findViewById(R.id.title_center_layout);
        mTvTitle = (TextView) top.findViewById(R.id.top_title_content);
        mImageViewCenter = (ImageView) top.findViewById(R.id.top_title_imageview);
        mImgLeft = (ImageView) top.findViewById(R.id.top_title_left_image);
        mLeftUnReadImg = (ImageView) top.findViewById(R.id.left_unread_image);
        mTvLeft = (TextView) top.findViewById(R.id.top_title_left_text);
        mImgRight = (ImageView) top.findViewById(R.id.top_title_right_image);
        mTvRight = (TextView) top.findViewById(R.id.top_title_right_text);
        mRightUnreadImg = (ImageView) top.findViewById(R.id.right_unread_image);
        mImgCenter = (ImageView) top.findViewById(R.id.top_title_center_image);
        mTvCenter = (TextView) top.findViewById(R.id.top_title_center_text);
        mUnReadImg = (ImageView) top.findViewById(R.id.unread_image);
        mDivideLine = top.findViewById(R.id.divide_line);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView);
        float widthSize = a.getDimension(R.styleable.TopTitleView_bothSideSize, 0);
        Drawable leftDrawable = a.getDrawable(R.styleable.TopTitleView_leftDrawable);
        if (leftDrawable != null) {
            mImgLeft.setImageDrawable(leftDrawable);
        }
        Drawable rightDrawable = a.getDrawable(R.styleable.TopTitleView_rightDrawable);
        if (rightDrawable != null) {
            mImgRight.setImageDrawable(rightDrawable);
        }
        Drawable centerDrawable = a.getDrawable(R.styleable.TopTitleView_centerDrawable);
        if (centerDrawable != null) {
            mImgCenter.setImageDrawable(centerDrawable);
        }
        CharSequence leftText = a.getText(R.styleable.TopTitleView_leftText);
        if (leftText != null) {
            mTvLeft.setText(leftText);
        }
        CharSequence rightText = a.getText(R.styleable.TopTitleView_rightText);
        if (rightText != null) {
            mTvRight.setText(rightText);
        }

        float margin = a.getDimension(R.styleable.TopTitleView_rightPadding, 24);
        mTvRight.setPadding(0, 0, (int) margin, 0);
        CharSequence centerText = a.getText(R.styleable.TopTitleView_centerText);
        if (centerText != null) {
            mTvCenter.setText(centerText);
        }
        CharSequence title = a.getText(R.styleable.TopTitleView_title);
        if (title != null) {
            mTvTitle.setText(title);
        }
        Drawable centerImage = a.getDrawable(R.styleable.TopTitleView_centerImage);
        if (centerImage != null) {
            mImageViewCenter.setImageDrawable(centerImage);
        }
        int titleColor = a.getColor(R.styleable.TopTitleView_topTitleColor, 0XFFFFFFFF);
        int rightTextColor = a.getColor(R.styleable.TopTitleView_rightTextColor, 0XFFFFFFFF);
        mTvTitle.setTextColor(titleColor);
        mTvRight.setTextColor(rightTextColor);
        float titleSize = a.getDimension(R.styleable.TopTitleView_topTitleSize, 18);
        if (titleSize > 18) {
            mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        } else {
            mTvTitle.setTextSize(titleSize);
        }
        mLeftLayout.setMinimumWidth((int) widthSize);
        mRightLayout.setMinimumWidth((int) widthSize);
        mCenterLayout.setMinimumWidth((int) widthSize);
        mMiddleLayout.setMinimumWidth((int) widthSize);
        a.recycle();
        setDefaultClick();
        this.addView(top);
        if (this.getBackground() == null) {
            this.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void setUnreadVisible(boolean visible) {
        if (visible) {
            mUnReadImg.setVisibility(View.VISIBLE);
        } else {
            mUnReadImg.setVisibility(View.GONE);
        }
    }

    public void setLeftUnreadVisible(boolean visible) {
        if (visible) {
            mLeftUnReadImg.setVisibility(View.VISIBLE);
        } else {
            mLeftUnReadImg.setVisibility(View.GONE);
        }
    }

    public void setRightUnreadVisible(boolean visible) {
        if (visible) {
            mRightUnreadImg.setVisibility(View.VISIBLE);
        } else {
            mRightUnreadImg.setVisibility(View.GONE);
        }
    }

    private void setDefaultClick() {
        mLeftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setRightLayoutWidth(int dimenId) {
        mRightLayout.setMinimumWidth(getResources().getDimensionPixelSize(dimenId));
    }

    public String getRightText() {
        return mTvRight.getText().toString();
    }

    public void setLeftOnClickListener(View.OnClickListener listener) {
        this.mLeftLayout.setOnClickListener(listener);
    }

    public void setRightOnClickListener(View.OnClickListener listener) {
        this.mRightLayout.setOnClickListener(listener);
    }

    public void setCenterOnClickListener(View.OnClickListener listener) {
        this.mCenterLayout.setOnClickListener(listener);
    }

    public void setMiddleOnClickListener(View.OnClickListener listener) {
        mMiddleLayout.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitle(Spanned title) {
        mTvTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTvTitle.setText(resId);
    }

    public void setTitleColor(int resId) {
        mTvTitle.setTextColor(resId);
    }

    public void setTitleBg(int resId) {
        mTvTitle.setBackgroundResource(resId);
    }

    public void setTvTitlePadding(int leftRight, int topDown) {
        mTvTitle.setPadding(leftRight, topDown, leftRight, topDown);
    }

    public void setTitleOnClickListener(OnClickListener listener) {
        mTvTitle.setOnClickListener(listener);
    }

    public void setTitleSize(int size) {
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setTitleStyle(int style) {
        mTvTitle.setTypeface(null, style);
    }

    public void setLeftImage(int resId) {
        mImgLeft.setImageResource(resId);
    }

    public void setLeftText(int resId) {
        mTvLeft.setText(resId);
    }

    public void setRightImage(int resId) {
        mImgRight.setImageResource(resId);
    }

    public void setRightText(int resId) {
        mTvRight.setText(getResources().getString(resId));
    }
    public void setRightBackGround(int resId) {
        mTvRight.setPadding(DensityUtil.dip2px(getContext(),8),DensityUtil.dip2px(getContext(),3),DensityUtil.dip2px(getContext(),8),DensityUtil.dip2px(getContext(),3));
        mTvRight.setGravity(Gravity.CENTER);
        mTvRight.setBackgroundResource(resId);
    }

    public void setRightTextColor(int resId) {
        mTvRight.setTextColor(resId);
    }

    public void setBackVisible(boolean visible) {
        if (visible) {
            mLeftLayout.setVisibility(VISIBLE);
        } else {
            mLeftLayout.setVisibility(GONE);
        }

    }

    public void setTitleVisible(boolean visible) {
        if (visible) {
            mTvTitle.setVisibility(VISIBLE);
        } else {
            mTvTitle.setVisibility(GONE);
        }
    }

    public void setRightLayoutGone() {
        mRightLayout.setVisibility(GONE);
    }

    public void setRightLayoutVisible() {
        mRightLayout.setVisibility(VISIBLE);
    }

    public void setDivideGone() {
        mDivideLine.setVisibility(GONE);
    }

    public void setRightLayoutPadding(int id) {
        mRightLayout.setPadding(0, 0, getResources().getDimensionPixelSize(id), 0);
    }
}
