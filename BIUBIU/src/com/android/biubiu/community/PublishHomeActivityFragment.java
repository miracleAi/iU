package com.android.biubiu.community;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.common.Constant;

import java.util.ArrayList;

import cc.imeetu.iu.R;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class PublishHomeActivityFragment extends BaseFragment {
    private View rootView;
    private LinearLayout textLayout;
    private LinearLayout imgLayout;
    private ImageView cancelImv;

    private static final int MUTI_PHOTO_REQUEST = 1001;

    private ArrayList<String> mSelectPath;

    public PublishHomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_publish_home, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        textLayout = (LinearLayout) rootView.findViewById(R.id.publish_text_layout);
        imgLayout = (LinearLayout) rootView.findViewById(R.id.publish_img_layout);
        cancelImv = (ImageView) rootView.findViewById(R.id.publish_cancel_imv);

        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PublishEditActivity.class);
                intent.putExtra(Constant.PUBLISH_TYPE,Constant.PUBLISH_TEXT);
                startActivity(intent);
            }
        });
        imgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
                boolean showCamera = false;

                int maxNum = 9;
                Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
                // 默认选择
                /*if (mSelectPath != null && mSelectPath.size() > 0) {
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                }*/
                startActivityForResult(intent,MUTI_PHOTO_REQUEST);
            }
        });
        cancelImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MUTI_PHOTO_REQUEST:
                if(resultCode == getActivity().RESULT_OK){
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    Intent intent = new Intent(getActivity(),PublishEditActivity.class);
                    intent.putExtra(Constant.PUBLISH_TYPE,Constant.PUBLISH_IMG);
                    intent.putStringArrayListExtra(Constant.PUBLISH_IMG_PATH,mSelectPath);
                    startActivity(intent);
                }
                break;
        }
    }
}
