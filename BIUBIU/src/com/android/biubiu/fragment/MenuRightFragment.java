package com.android.biubiu.fragment;

import com.android.biubiu.component.indicator.FragmentIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MenuRightFragment extends Fragment implements FragmentIndicator.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTabClick() {

    }

    @Override
    public void onLeaveTab() {

    }
}
