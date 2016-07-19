package com.android.biubiu.ui.conversation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 * 包括iU小秘书、iU恋人、合约CP
 */
public class CoupleFragment extends Fragment {


    public CoupleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_couple, container, false);
    }

}
