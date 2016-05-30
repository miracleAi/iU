package com.android.biubiu.community;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PublishEditActivityFragment extends Fragment {
    private View rootView;
    private TextView tagTv;
    private EditText contentEt;
    private TextView textCountTv;
    private ViewPager photoPager;
    private TopTitleView titleView;
    private LinearLayout photoLayout;

    private PublishPagerAdapter imgPagerAdapter;
    private ArrayList<String> mSelectPath;

    private static final int REQUEST_TAG = 1001;

    ImageOptions imageOptions;

    public PublishEditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_publish_edit, container, false);
        initView();
        getInfo();
        return rootView;
    }

    private void initView() {
        tagTv = (TextView) rootView.findViewById(R.id.tag_tv);
        contentEt = (EditText) rootView.findViewById(R.id.content_et);
        textCountTv = (TextView) rootView.findViewById(R.id.text_count_tv);
        photoPager = (ViewPager) rootView.findViewById(R.id.photo_pager);
        photoLayout = (LinearLayout) rootView.findViewById(R.id.photo_layout);
        titleView = (TopTitleView) rootView.findViewById(R.id.top_title_view);

        titleView.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        titleView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/27  上传图片  发布帖子 
            }
        });

        tagTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CardTagActivity.class);
                startActivityForResult(intent,REQUEST_TAG);
            }
        });
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.loadingbbbb)
                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(false)
                .build();
    }

    private void setImgs(){
        imgPagerAdapter = new PublishPagerAdapter(getActivity(),mSelectPath,imageOptions);
    }

    public void getInfo() {
        Bundle b = getArguments();
        String typeStr = b.getString(Constant.PUBLISH_TYPE);
        if(typeStr.equals(Constant.PUBLISH_IMG)){
            mSelectPath = b.getStringArrayList(Constant.PUBLISH_IMG_PATH);
            photoLayout.setVisibility(View.VISIBLE);
            // TODO: 2016/5/27 展示选择图片
        }else{
            photoLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAG:
                break;
        }
    }
}
