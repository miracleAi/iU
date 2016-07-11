package com.android.biubiu.community;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.biubiu.BaseFragment;
import com.android.biubiu.bean.ImageBean;
import com.android.biubiu.bean.TagBean;
import com.android.biubiu.callback.HttpCallback;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.HttpRequestUtils;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.NetUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.ant.liao.GifView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

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
    private TopTitleView titleView;
    private LinearLayout photoLayout;
    private LinearLayout loadingLayout;
    private GifView loadGif;
    private TextView loadTv;
    private LinearLayout photoScrollLayout;

    private ArrayList<String> mSelectPath;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<Integer> tagIdList = new ArrayList<Integer>();
    private String userCode = "";

    //上传文件相关
    String accessKeyId = "";
    String accessKeySecret = "";
    String securityToken = "";
    String expiration = "";

    private int imgPos = 0;

    private static final int REQUEST_TAG = 1001;

    ImageOptions imageOptions;

    public PublishEditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_publish_edit, container, false);
        userCode = SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, "");
        initView();
        getInfo();
        titleView.setRightBackGround(R.drawable.title_btn_disable);
        titleView.setRightTextColor(getResources().getColor(R.color.white));
        return rootView;
    }

    private void initView() {
        tagTv = (TextView) rootView.findViewById(R.id.tag_tv);
        contentEt = (EditText) rootView.findViewById(R.id.content_et);
        textCountTv = (TextView) rootView.findViewById(R.id.text_count_tv);
        photoScrollLayout = (LinearLayout) rootView.findViewById(R.id.photo_scroll_layout);
        photoLayout = (LinearLayout) rootView.findViewById(R.id.photo_layout);
        titleView = (TopTitleView) rootView.findViewById(R.id.top_title_view);

        titleView.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        titleView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == tagIdList || tagIdList.size() == 0) {
                    Toast.makeText(getActivity(), "标签不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(contentEt.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (mSelectPath != null && mSelectPath.size() > 0) {
                    getOssToken();
                } else {
                    if (null != contentEt.getText() && !"".equals(contentEt.getText().toString())) {
                        publishPost(1);
                    } else {
                        Toast.makeText(getActivity(), "帖子内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tagTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardTagActivity.class);
                intent.putExtra(Constant.TO_TAG_TYPE, Constant.TAG_TYPE_PUBLISH);
                startActivityForResult(intent, REQUEST_TAG);
            }
        });
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    textCountTv.setText((300 - s.length()) + "");
                    completeState();
                } else {
                    textCountTv.setText("300");
                }
            }
        });
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.loadingbbbb)
                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(false)
                .build();
    }

    private void completeState() {
        if (null == tagIdList || tagIdList.size() == 0) {
            titleView.setRightBackGround(R.drawable.title_btn_disable);
            titleView.setRightTextColor(getResources().getColor(R.color.white));
            return;
        }
        if (mSelectPath != null && mSelectPath.size() > 0) {
            titleView.setRightBackGround(R.drawable.biu_btn_white);
            titleView.setRightTextColor(getResources().getColor(R.color.main_green));
        } else {
            if (null != contentEt.getText() && !"".equals(contentEt.getText().toString())) {
                titleView.setRightBackGround(R.drawable.biu_btn_white);
                titleView.setRightTextColor(getResources().getColor(R.color.main_green));
            } else {
                titleView.setRightBackGround(R.drawable.title_btn_disable);
                titleView.setRightTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void setImgs(int i) {
        ImageView imgView = new ImageView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(getActivity(), 70), DensityUtil.dip2px(getActivity(), 70));
        lp.setMargins(DensityUtil.dip2px(getActivity(), 10), 0, 0, 0);
        imgView.setLayoutParams(lp);
        imgView.setId(i);
       // imgView.setPadding(DensityUtil.dip2px(getActivity(), 10), 0, 0, 0);
        x.image().bind(imgView, mSelectPath.get(i), imageOptions);
        photoScrollLayout.addView(imgView);
    }

    public void getInfo() {
        Bundle b = getArguments();
        String typeStr = b.getString(Constant.PUBLISH_TYPE);
        if (typeStr.equals(Constant.PUBLISH_IMG)) {
            mSelectPath = b.getStringArrayList(Constant.PUBLISH_IMG_PATH);
            photoLayout.setVisibility(View.VISIBLE);
            if (mSelectPath.size() > 0) {
                for (int i = 0; i < mSelectPath.size(); i++) {
                    setImgs(i);
                }
            }
        } else {
            photoLayout.setVisibility(View.GONE);
        }
        if(null != b.getSerializable(Constant.TAG)){
            TagBean publishTag = (TagBean) b.getSerializable(Constant.TAG);
            tagTv.setText(publishTag.getContent());
            tagIdList.clear();
            tagIdList.add(publishTag.getId());
            completeState();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAG:
                if (resultCode == getActivity().RESULT_OK) {
                    TagBean publishTag = (TagBean) data.getSerializableExtra("tagBean");
                    tagTv.setText(publishTag.getContent());
                    tagIdList.clear();
                    tagIdList.add(publishTag.getId());
                    completeState();
                }
                break;
        }
    }

    //鉴权
    public void getOssToken() {
        if (!NetUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingLayout("正在提交……");
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.REGISTER_OSS);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                // TODO Auto-generated method stub
                dismissLoadingLayout();
                Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
                LogUtil.d("mytest", "error--" + ex.getMessage());
                LogUtil.d("mytest", "error--" + ex.getCause());
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                LogUtil.d("mytest", "ret==" + arg0);
                try {
                    JSONObject jsonObjs = new JSONObject(arg0);
                    String state = jsonObjs.getString("state");
                    if (!state.equals("200")) {
                        dismissLoadingLayout();
                        Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject obj = jsonObjs.getJSONObject("data");
                    //JSONObject obj = new JSONObject(jsonObjs.getString("data"));
                    accessKeyId = obj.getString("accessKeyId");
                    accessKeySecret = obj.getString("accessKeySecret");
                    securityToken = obj.getString("securityToken");
                    expiration = obj.getString("expiration");
                    asyncPutObjectFromLocalFile(0);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    // 从本地文件上传，使用非阻塞的异步接口
    public void asyncPutObjectFromLocalFile(final int postion) {
        String endpoint = HttpContants.A_LI_YUN;
        //OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("XWp6VLND94vZ8WNJ", "DSi9RRCv4bCmJQZOOlnEqCefW4l1eP");
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {

                return new OSSFederationToken(accessKeyId, accessKeySecret, securityToken, expiration);
            }
        };
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(getActivity(), endpoint, credentialProvider, conf);
        final String fileName = "community/post/img/" + userCode + "_" + System.currentTimeMillis() / 1000 + "_" + postion + ".jpeg";
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("protect-app", fileName, mSelectPath.get(postion));

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
                addToImageList(mSelectPath.get(postion), fileName);
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                imgPos = imgPos + 1;
                if (imgPos < mSelectPath.size()) {
                    asyncPutObjectFromLocalFile(imgPos);
                } else {
                    publishPost(1);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissLoadingLayout();
                Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
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

    private void addToImageList(String path, String fileName) {
        ImageBean bean = new ImageBean();
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        bean.setH(options.outHeight);
        bean.setW(options.outWidth);
        bean.setUrl(fileName);
        Gson gson = new Gson();
        imageList.add(gson.toJson(bean));
    }

    //发布帖子
    private void publishPost(int type) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("type", type);
            requestObject.put("tags", tagIdList);
            requestObject.put("imgs", imageList);
            requestObject.put("content", contentEt.getText().toString());
            requestObject.put("music", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestUtils.commonRequest(getActivity(), requestObject, HttpContants.PUBLISH_POST, new HttpCallback() {
            @Override
            public void callback(JSONObject object, String error) {
                dismissLoadingLayout();
                if (object != null) {
                    Toast.makeText(getActivity(), "帖子发布成功", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Constant.PUBLISH_POST_ACTION);
                    getActivity().sendBroadcast(i, Constant.PUBLISH_POST_ACTION_PERMISSION);
                    getActivity().setResult(getActivity().RESULT_OK);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "帖子发布失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //显示加载中
    public void showLoadingLayout(String loadingStr) {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) rootView.findViewById(R.id.loading_layout);
        }
        if (loadGif == null) {
            loadGif = (GifView) rootView.findViewById(R.id.load_gif);
            loadGif.setGifImage(R.drawable.loadingbbbb);
            loadGif.setShowDimension(DensityUtil.dip2px(getActivity(), 30), DensityUtil.dip2px(getActivity(), 30));
            loadGif.setGifImageType(GifView.GifImageType.COVER);
        }
        if (loadTv == null) {
            loadTv = (TextView) rootView.findViewById(R.id.loading_tv);
        }
        loadTv.setText(loadingStr);
        loadGif.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    //加载完毕隐藏
    public void dismissLoadingLayout() {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) rootView.findViewById(R.id.loading_layout);
        }
        loadingLayout.setVisibility(View.GONE);
    }

    //显示提示退出对话框
    public void showDialog() {
        if (contentEt.getText() != null && !contentEt.getText().toString().equals("") || imageList.size() > 0 || tagIdList.size() > 0) {
            CommonDialog.doubleBtnDialog(getActivity(), getResources().getString(R.string.exit_edit_title),
                    getResources().getString(R.string.exit_edit_tips),
                    getResources().getString(R.string.cancel),
                    getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
        } else {
            getActivity().finish();
        }
    }

}
