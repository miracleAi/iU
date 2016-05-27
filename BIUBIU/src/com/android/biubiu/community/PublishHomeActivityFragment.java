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
public class PublishHomeActivityFragment extends Fragment {

    public PublishHomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish_home, container, false);
    }
}
