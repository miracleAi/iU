package com.android.biubiu.activity.mine;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import cc.imeetu.iu.R;

public class MainSetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_set);
    }

}
