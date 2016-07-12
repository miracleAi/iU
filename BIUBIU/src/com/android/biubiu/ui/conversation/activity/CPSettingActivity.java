package com.android.biubiu.ui.conversation.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.biubiu.common.city.ArrayWheelAdapter;
import com.android.biubiu.common.city.OnWheelChangedListener;
import com.android.biubiu.common.city.WheelView;
import com.android.biubiu.component.title.TopTitleView;

import cc.imeetu.iu.R;

public class CPSettingActivity extends Activity implements OnWheelChangedListener, View.OnClickListener {
    private View mEntireView;
    private TopTitleView mTopTitle;
    private RelativeLayout mAgreeLayout, mChargeLayout;
    private ImageView mAgreeToggle;
    private boolean mIsAgreeCharge;

    private String[] mUCoinArray;
    private PopupWindow mPopupWindow;
    private TextView mCancleTv, mDoneTv, mCharmTv;
    private WheelView mUcoinWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpsetting);
        initView();
        initData();
    }

    private void initView() {
        mEntireView = findViewById(R.id.entire_view);
        mTopTitle = (TopTitleView) findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAgreeLayout = (RelativeLayout) findViewById(R.id.agree_toggle_layout);
        mAgreeLayout.setOnClickListener(this);
        mAgreeToggle = (ImageView) findViewById(R.id.agree_toggle);

        mChargeLayout = (RelativeLayout) findViewById(R.id.charge_layout);
        mChargeLayout.setOnClickListener(this);

        View contentView = LayoutInflater.from(this).inflate(R.layout.u_coin_select_layout, null);
        mCancleTv = (TextView) contentView.findViewById(R.id.cancel_layout);
        mCancleTv.setOnClickListener(this);
        mDoneTv = (TextView) contentView.findViewById(R.id.done_layout);
        mDoneTv.setOnClickListener(this);
        mCharmTv = (TextView) contentView.findViewById(R.id.charm_level_textview);
        mUcoinWheel = (WheelView) contentView.findViewById(R.id.ucoin_wheel);
        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x88000000));
    }

    private void initData() {
        mUCoinArray = getResources().getStringArray(R.array.u_coin_array);
        mUcoinWheel.setViewAdapter(new ArrayWheelAdapter<String>(this, mUCoinArray));
        mUcoinWheel.setVisibleItems(3);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        mCharmTv.setText(mUCoinArray[mUcoinWheel.getCurrentItem()]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_toggle_layout:
                break;
            case R.id.charge_layout:
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.showAtLocation(mEntireView, Gravity.BOTTOM, 0, 0);
                }
                break;
        }
    }
}
