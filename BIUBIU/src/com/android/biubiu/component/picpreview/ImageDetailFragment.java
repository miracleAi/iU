package com.android.biubiu.component.picpreview;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

import cc.imeetu.iu.R;

public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    ImageOptions comOption;
//    ImageLoader imgLoader;
    private Bitmap largeBitmap;
    public PicLoaderCallBack mPicCaller = null;
    public ArrayList<Bitmap> mImgList;
    public int mIndex;

    public interface PicLoaderCallBack {
        void bmpLoaded(String imageUri, Bitmap loadedImage);
    }

    public static ImageDetailFragment newInstance(String imageUrl) {
        ImageDetailFragment f = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*comOption = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .bitmapConfig(Bitmap.Config.ARGB_8888)
//                         .showImageOnLoading(R.drawable.neirongtu1)//加载开始默认的图片
//                        .showImageForEmptyUri(R.drawable.lst_default_pic)// url爲空會显示该图片，自己放在drawable里面的
//                        .showImageOnFail(R.drawable.lst_default_pic)// 加载图片出现问题，会显示该图片
                        .cacheInMemory(true)// 缓存用
                        .cacheOnDisk(true)// 缓存用
                        .build();*/
        comOption = new ImageOptions.Builder()
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)

                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(true)
                .build();
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d("image", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
//        imgLoader.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d("image", "onDestroy");
        mImgList.set(mIndex, null);
        mImageView.setImageDrawable(null);
        // if(largeBitmap!=null){
        // if(!largeBitmap.isRecycled()){
        // largeBitmap.recycle();
        // Log.d("image", "is recycle = onDestroy index" + mIndex + ";bitmap recycle" + largeBitmap);
        //
        //
        // }
        // }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*imgLoader.displayImage(mImageUrl, mImageView, comOption, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "下载错误";
                        break;
                    case DECODING_ERROR:
                        message = "图片无法显示";
                        break;
                    case NETWORK_DENIED:
                        message = "网络有问题，无法下载";
                        break;
                    case OUT_OF_MEMORY:
                        message = "图片太大无法显示";
                        break;
                    case UNKNOWN:
                        message = "未知的错误";
                        break;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                mAttacher.update();
                largeBitmap = loadedImage;
                mImgList.set(mIndex, largeBitmap);
                if (mPicCaller != null) {
                    mPicCaller.bmpLoaded(imageUri, loadedImage);
                }
            }
        });*/
        progressBar.setVisibility(View.VISIBLE);
        x.image().bind(mImageView, mImageUrl, comOption, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                BitmapDrawable bd = (BitmapDrawable) drawable;
                Bitmap loadedImage = bd.getBitmap();
                mAttacher.update();
                largeBitmap = loadedImage;
                mImgList.set(mIndex, largeBitmap);
                if (mPicCaller != null) {
                    mPicCaller.bmpLoaded(mImageUrl, loadedImage);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
