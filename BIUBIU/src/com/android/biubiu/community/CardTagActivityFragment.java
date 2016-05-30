package com.android.biubiu.community;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardTagActivityFragment extends Fragment {
    private View rootView;
    public CardTagActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_card_tag, container, false);
        return rootView;
    }
}
