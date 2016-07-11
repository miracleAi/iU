package com.android.biubiu.community;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import com.android.biubiu.BaseActivity;
import com.android.biubiu.common.Constant;

import cc.imeetu.iu.R;

public class PublishHomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish_home);
        PublishHomeActivityFragment fragment = new PublishHomeActivityFragment();
        if(null != getIntent().getSerializableExtra(Constant.TAG)){
            Bundle b = new Bundle();
            b.putSerializable(Constant.TAG,getIntent().getSerializableExtra(Constant.TAG));
            fragment.setArguments(b);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.layout_body, fragment).commit();
    }

}
