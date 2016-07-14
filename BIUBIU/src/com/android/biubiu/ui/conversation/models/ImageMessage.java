package com.android.biubiu.ui.conversation.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.biubiu.application.BiubiuApplication;
import com.android.biubiu.component.util.FileUtil;
import com.android.biubiu.ui.conversation.adapter.ChatAdapter;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMImageType;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.io.IOException;

import cc.imeetu.iu.R;


/**
 * 图片消息数据
 */
public class ImageMessage extends Message {

    private static final String TAG = "ImageMessage";

    public ImageMessage(TIMMessage message){
        this.message = message;
    }

    public ImageMessage(String path){
        message = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        if (message.addElement(elem) != 0) return;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        TIMImageElem e = (TIMImageElem) message.getElement(0);
        switch (message.status()){
            case Sending:

                ImageView imageView = new ImageView(BiubiuApplication.getInstance());
                imageView.setImageBitmap(getThumb(e.getPath()));
                clearView(viewHolder);
                getBubbleView(viewHolder).addView(imageView);
                break;
            case SendSucc:
                for(TIMImage image : e.getImageList()) {
                    if (image.getType() == TIMImageType.Thumb){
                        final String uuid = image.getUuid();
                        if (FileUtil.isCacheFileExist(uuid)){
                            showThumb(viewHolder,uuid);
                        }else{
                            image.getImage(new TIMValueCallBack<byte[]>() {
                                @Override
                                public void onError(int code, String desc) {//获取图片失败
                                    //错误码code和错误描述desc，可用于定位请求失败原因
                                    //错误码code含义请参见错误码表
                                    Log.e(TAG, "getImage failed. code: " + code + " errmsg: " + desc);
                                }

                                @Override
                                public void onSuccess(byte[] data) {//成功，参数为图片数据
                                    FileUtil.createFile(data, uuid);
                                    showThumb(viewHolder,uuid);
                                }
                            });
                        }
                    }
                    if (image.getType() == TIMImageType.Original){
                        final String uuid = image.getUuid();
                        if (FileUtil.isCacheFileExist(uuid)){
                            setImageEvent(viewHolder,uuid,context);
                        }else{
                            image.getImage(new TIMValueCallBack<byte[]>() {
                                @Override
                                public void onError(int code, String desc) {//获取图片失败
                                    //错误码code和错误描述desc，可用于定位请求失败原因
                                    //错误码code含义请参见错误码表
                                    Log.e(TAG, "getImage failed. code: " + code + " errmsg: " + desc);
                                }

                                @Override
                                public void onSuccess(byte[] data) {//成功，参数为图片数据
                                    FileUtil.createFile(data, uuid);
                                    setImageEvent(viewHolder, uuid,context);
                                }
                            });
                        }

                    }
                }
                break;
        }
        showStatus(viewHolder);


    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return BiubiuApplication.getInstance().getString(R.string.summary_image);
    }

    /**
     * 生成缩略图
     * 缩略图是将原图等比压缩，压缩后宽、高中较小的一个等于198像素
     * 详细信息参见文档
     */
    private Bitmap getThumb(String path){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width=options.outWidth, height=options.outHeight;
        if (width > height){
            reqWidth = 198;
            reqHeight = (reqWidth * height)/width;
        }else{
            reqHeight = 198;
            reqWidth = (width * reqHeight)/height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try{
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface ei =  new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        }catch (IOException e){
            return null;
        }
    }

    private void showThumb(final ChatAdapter.ViewHolder viewHolder,String filename){
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getCacheFilePath(filename));
        ImageView imageView = new ImageView(BiubiuApplication.getInstance());
        imageView.setImageBitmap(bitmap);
        getBubbleView(viewHolder).addView(imageView);
    }

    private void setImageEvent(final ChatAdapter.ViewHolder viewHolder, final String fileName,final Context context){
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("filename",fileName);
                context.startActivity(intent);*/
            }
        });
    }
}
