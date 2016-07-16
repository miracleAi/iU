package com.android.biubiu.ui.mine.child;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.biubiu.ui.base.BaseActivity;

import cc.imeetu.iu.R;

public class CharmActivity extends BaseActivity {
    private ImageView headImv;
    private TextView levelTv;
    private TextView nameTv;
    private TextView minLevelTv;
    private TextView maxLevelTv;
    private ProgressBar levelBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charm);
        initView();
    }

    private void initView() {
        headImv = (ImageView) findViewById(R.id.head_imv);
        levelTv = (TextView) findViewById(R.id.charm_level_tv);
        nameTv = (TextView) findViewById(R.id.name_tv);
        minLevelTv = (TextView) findViewById(R.id.min_charm_value);
        maxLevelTv = (TextView) findViewById(R.id.max_charm_value);
        levelBar = (ProgressBar) findViewById(R.id.level_bar);
    }

}
