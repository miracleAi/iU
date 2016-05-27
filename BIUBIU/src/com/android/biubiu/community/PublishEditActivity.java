package com.android.biubiu.community;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import com.android.biubiu.common.Constant;

import cc.imeetu.iu.R;

public class PublishEditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish_edit);
        String typeStr = getIntent().getStringExtra(Constant.PUBLISH_TYPE);
        PublishEditActivityFragment fragment = new PublishEditActivityFragment();
        Bundle b = new Bundle();
        b.putString(Constant.PUBLISH_TYPE,typeStr);
        if(typeStr.equals(Constant.PUBLISH_IMG)){
            b.putStringArrayList(Constant.PUBLISH_IMG_PATH,getIntent().getStringArrayListExtra(Constant.PUBLISH_IMG_PATH));
        }
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.layout_body,fragment).commit();
    }

}
