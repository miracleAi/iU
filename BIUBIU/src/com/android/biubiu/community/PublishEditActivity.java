package com.android.biubiu.community;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import com.android.biubiu.common.Constant;

import cc.imeetu.iu.R;

public class PublishEditActivity extends Activity {
    PublishEditActivityFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish_edit);
        String typeStr = getIntent().getStringExtra(Constant.PUBLISH_TYPE);
        fragment = new PublishEditActivityFragment();
        Bundle b = new Bundle();
        b.putString(Constant.PUBLISH_TYPE,typeStr);
        if(typeStr.equals(Constant.PUBLISH_IMG)){
            b.putStringArrayList(Constant.PUBLISH_IMG_PATH,getIntent().getStringArrayListExtra(Constant.PUBLISH_IMG_PATH));
        }
        if(null != getIntent().getSerializableExtra(Constant.TAG)){
            b.putSerializable(Constant.TAG,getIntent().getSerializableExtra(Constant.TAG));
        }
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.layout_body,fragment).commit();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(fragment != null){
            fragment.showDialog();
        }
    }
}
