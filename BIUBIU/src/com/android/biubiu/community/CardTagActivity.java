package com.android.biubiu.community;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import com.android.biubiu.common.Constant;

import cc.imeetu.iu.R;

public class CardTagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_card_tag);
        CardTagActivityFragment fragment = new CardTagActivityFragment();
        Bundle b = new Bundle();
        b.putString(Constant.TO_TAG_TYPE,getIntent().getStringExtra(Constant.TO_TAG_TYPE));
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.layout_body,fragment).commit();
    }

}
