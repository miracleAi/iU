package com.android.biubiu.activity.mine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.imeetu.iu.R;

import com.android.biubiu.BaseActivity;
import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.adapter.ScanPagerAdapter;
import com.android.biubiu.bean.UserPhotoBean;
import com.android.biubiu.component.picpreview.HackyViewPager;
import com.android.biubiu.component.picpreview.ImageDetailFragment;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;

public class UserPhotoScanActivity extends BaseActivity implements OnClickListener {
    private RelativeLayout backRl;
    private TextView indexTv;
    private RelativeLayout deleteRl;
    private HackyViewPager photoPager;
    private ArrayList<UserPhotoBean> photoList = new ArrayList<UserPhotoBean>();
    private int currentIndex = 0;
    private ScanPagerAdapter scanAdapter;
    private ImagePagerAdapter mAdapter;
    ImageOptions imageOptions;
    boolean hasDelete = false;
    boolean isMyself = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userphoto_scan_layout);
        getIntentData();
        initView();
        setPager();
        x.image().bind(new ImageView(this), "", imageOptions, new CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bd.getBitmap();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getIntentData() {
        // TODO Auto-generated method stub
        ArrayList<UserPhotoBean> list = (ArrayList<UserPhotoBean>) getIntent().getSerializableExtra("photolist");
        if (list != null && list.size() > 0) {
            photoList.addAll(list);
        }
        currentIndex = getIntent().getIntExtra("photoindex", 0);
        isMyself = getIntent().getBooleanExtra("isMyself", false);
    }

    private void initView() {
        // TODO Auto-generated method stub
        backRl = (RelativeLayout) findViewById(R.id.title_back_rl);
        backRl.setOnClickListener(this);
        indexTv = (TextView) findViewById(R.id.photo_index_tv);
        deleteRl = (RelativeLayout) findViewById(R.id.delete_rl);
        photoPager = (HackyViewPager) findViewById(R.id.userphoto_scan_pager);

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.loadingbbbb)
                .setIgnoreGif(true)
                .setFailureDrawableId(R.drawable.photo_imageview_fail)
                .build();
        if (isMyself) {
            deleteRl.setVisibility(View.VISIBLE);
            deleteRl.setOnClickListener(this);
        } else {
            deleteRl.setVisibility(View.GONE);
        }
    }

    private void setPager() {
        indexTv.setText((currentIndex + 1) + "/" + photoList.size());
        photoPager.setOffscreenPageLimit(3);
//        scanAdapter = new ScanPagerAdapter(getApplicationContext(), photoList, imageOptions);

        List<String> imgUrls = new ArrayList<String>();
        for(UserPhotoBean bean:photoList){
            imgUrls.add(bean.getPhotoOrigin());
        }
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(),imgUrls);
        photoPager.setAdapter(mAdapter);
        photoPager.setCurrentItem(currentIndex);
        photoPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentIndex = arg0;
                indexTv.setText((currentIndex + 1) + "/" + photoList.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void deletePhoto() {
        // TODO Auto-generated method stub
        String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, "");
        String fileCode = photoList.get(currentIndex).getPhotoCode();
        String filename = photoList.get(currentIndex).getPhotoName();
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.DELETE_PHOTO);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("device_code", deviceId);
            requestObject.put("photo_code", fileCode);
            requestObject.put("photo_name", filename);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                // TODO Auto-generated method stub
                LogUtil.d("mytest", "error--" + ex.getMessage());
                LogUtil.d("mytest", "error--" + ex.getCause());
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                hasDelete = true;
                LogUtil.d("mytest", "deleteph==" + result);
                try {
                    JSONObject jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        toastShort("删除照片失败");
                        return;
                    }
                    setResult(RESULT_OK);
                    JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                    photoList.remove(currentIndex);
                    scanAdapter = new ScanPagerAdapter(getApplicationContext(), photoList, imageOptions);
                    photoPager.setAdapter(scanAdapter);
                    if (photoList.size() == 0) {
                        /*Intent intent = new Intent(UserPhotoScanActivity.this, MyPagerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/
                        finish();
                    } else if (currentIndex > (photoList.size() - 1)) {
                        photoPager.setCurrentItem(currentIndex - 1);
                        currentIndex = currentIndex - 1;
                        indexTv.setText((currentIndex + 1) + "/" + photoList.size());
                    } else {
                        photoPager.setCurrentItem(currentIndex);
                        indexTv.setText((currentIndex + 1) + "/" + photoList.size());
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.title_back_rl:
                if (hasDelete) {
                    /*Intent intent = new Intent(UserPhotoScanActivity.this, MyPagerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.delete_rl:
                deletePhoto();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*if (hasDelete) {
                Intent intent = new Intent(UserPhotoScanActivity.this, MyPagerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }*/
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public List<String> fileList;
        public ArrayList<Bitmap> mImgList;

        public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
            super(fm);
            this.fileList = fileList;
            mImgList = new ArrayList<Bitmap>();
            for (int i = 0; i < getCount(); i++) {
                mImgList.add(null);
            }
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            ImageDetailFragment frag = ImageDetailFragment.newInstance(url);
            frag.setRetainInstance(true);
            frag.mImgList = mImgList;
            frag.mIndex = position;
            return frag;
        }

    }

}
