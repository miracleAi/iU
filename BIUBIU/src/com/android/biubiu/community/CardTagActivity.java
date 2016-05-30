package com.android.biubiu.community;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import cc.imeetu.iu.R;

public class CardTagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_card_tag);
    }

}
