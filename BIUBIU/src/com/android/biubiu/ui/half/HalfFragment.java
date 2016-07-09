package com.android.biubiu.ui.half;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.component.indicator.FragmentIndicator;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalfFragment extends BaseFragment implements FragmentIndicator.OnClickListener{


    public HalfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_half, container, false);
    }

    @Override
    public void onTabClick() {

    }

    @Override
    public void onLeaveTab() {

    }
}
