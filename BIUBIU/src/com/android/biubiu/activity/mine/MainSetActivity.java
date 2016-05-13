package com.android.biubiu.activity.mine;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Window;

import cc.imeetu.iu.R;

public class MainSetActivity extends Activity {

    MainSetActivityFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_set);
        fragment = new MainSetActivityFragment();
        getFragmentManager().beginTransaction().add(R.id.set_layout_body, fragment).commit();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK){
            fragment.saveSetInfo();
        }
        return super.onKeyDown(keyCode, event);
    }
}
