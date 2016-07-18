package com.android.biubiu.ui.mine.child;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.LogUtil;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.ui.half.bean.HalfData;
import com.android.biubiu.ui.mine.bean.VerifyCodeData;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import cc.imeetu.iu.R;

public class VerifyActivity extends BaseActivity {
    private ImageView photoImv;
    private EditText codeEt;
    private Button completeBtn;
    private static final int SELECT_PHOTO = 1001;
    private String photoPath = "";
    private Bitmap photo = null;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateIdStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        initView();
    }

    private void initView() {
        photoImv = (ImageView) findViewById(R.id.photo_imv);
        codeEt = (EditText) findViewById(R.id.code_et);
        completeBtn = (Button) findViewById(R.id.complete_btn);
        completeBtn.setBackgroundResource(R.drawable.biu_btn_disabled);
        codeEt.addTextChangedListener(watcher);
        photoImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destoryBimap();
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeEt.getText() != null && codeEt.getText().length()>0){
                    verifyCode();
                }else if(null != photoPath && !"".equals(photoPath)){
                    uploadPhoto();
                }
            }
        });

    }
    private void updateIdStatus() {
            RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS+HttpContants.UPDATE_USETINFO);
            String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
            String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, "");
            JSONObject requestObject = new JSONObject();
            try {
                requestObject.put("token", token);
                requestObject.put("device_code", deviceId);
                requestObject.put("idStatus ",1);
                requestObject.put("parameters", "idStatus ");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            params.addBodyParameter("data", requestObject.toString());
            x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onCancelled(Callback.CancelledException arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(Throwable ex, boolean arg1) {
                    // TODO Auto-generated method stub
                    LogUtil.d("mytest", "error--"+ex.getMessage());
                    LogUtil.d("mytest", "error--"+ex.getCause());
                }

                @Override
                public void onFinished() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onSuccess(String result) {
                    // TODO Auto-generated method stub
                    dismissLoadingLayout();
                    LogUtil.d("mytest", "idstatus=="+result);
                    try {
                        JSONObject jsons = new JSONObject(result);
                        String state = jsons.getString("state");
                        if(!state.equals("200")){
                            dismissLoadingLayout();
                            toastShort("上传失败");
                            return ;
                        }
                        JSONObject data = jsons.getJSONObject("data");
                        toastShort("上传成功");
                        finish();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    private void verifyCode() {
        showLoadingLayout("正在验证……");
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS+HttpContants.VERIFY_REQUEST);
        String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("code",codeEt.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dismissLoadingLayout();
                Data<VerifyCodeData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<VerifyCodeData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()),VerifyActivity.this)) {
                    return;
                }
                VerifyCodeData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(VerifyActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                if(data.getVerifyRes() == 0){
                    if(null != photoPath && !"".equals(photoPath)){
                        uploadPhoto();
                    }else{
                        toastShort("验证失败");
                    }
                }else{
                    toastShort("验证成功");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissLoadingLayout();
                toastShort("验证失败……");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void uploadPhoto() {
        showLoadingLayout("正在上传……");
        String endpoint = HttpContants.A_LI_YUN;
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("XWp6VLND94vZ8WNJ", "DSi9RRCv4bCmJQZOOlnEqCefW4l1eP");
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);
        String user_code = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.USER_CODE, "");
        final String fileName = "profile/" + "user/"+user_code+"/mine/images/VerifyIdentity.jpeg";

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("protect-app", fileName, photoPath);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                dismissLoadingLayout();
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissLoadingLayout();
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if(s.length()>0){
                completeBtn.setBackgroundResource(R.drawable.biu_btn_normal);
            }else{
                if(null != photoPath && !"".equals(photoPath) ){
                    completeBtn.setBackgroundResource(R.drawable.biu_btn_normal);
                }else{
                    completeBtn.setBackgroundResource(R.drawable.biu_btn_disabled);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            Uri uri = data.getData();
            if (uri != null) {
                this.photo = BitmapFactory.decodeFile(uri.getPath());
            }
            if (this.photo == null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    this.photo = (Bitmap) bundle.get("data");
                } else {
                    Toast.makeText(VerifyActivity.this, "拍照失败", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            FileOutputStream fileOutputStream = null;
            try {
                // 获取 SD 卡根目录
                String saveDir = Environment.getExternalStorageDirectory() + "/meitian_photos";
                // 新建目录
                File dir = new File(saveDir);
                if (!dir.exists()) dir.mkdir();
                // 生成文件名
                SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
                String filename = System.currentTimeMillis() + ".jpg";
                // 新建文件
                File file = new File(saveDir, filename);
                // 打开文件输出流
                fileOutputStream = new FileOutputStream(file);
                // 生成图片文件
                this.photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                // 相片的完整路径
                this.photoPath = file.getPath();
                x.image().bind(photoImv,photoPath);
                completeBtn.setBackgroundResource(R.drawable.biu_btn_normal);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

/**
 * 销毁图片文件
 */
    private void destoryBimap()
    {
        if (photo != null && ! photo.isRecycled()) {
            photo.recycle();
            photo = null;
        }
    }
}
