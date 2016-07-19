package com.android.biubiu.ui.mine.child;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.component.util.LogUtil;
import com.android.biubiu.component.util.RecorderUtil;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cc.imeetu.iu.R;

public class MyVoiceActivity extends BaseActivity {
    private final String TAG = MyVoiceActivity.class.getName();
    private ImageView playImv;
    private TextView playTv;
    private RelativeLayout recordLayout;
    private TextView recordTv;
    private ImageView recordImv;
    private TopTitleView topTitle;
    boolean isPlaying = false;
    boolean isHoldVoice = false;
    private RecorderUtil recorder = new RecorderUtil();
    private int recordTime = 0;
    private Handler recordHandler;
    private MediaPlayer mPlayer;
    private String audioPath;
    private Handler playHandler;
    private int currentTime = 0;

    Handler stopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateVoiceTime(recordTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voice);
        recordHandler = new Handler();
        playHandler = new Handler();
        mPlayer = new MediaPlayer();
        initView();
    }

    private void initView() {
        topTitle = (TopTitleView) findViewById(R.id.top_title_view);
        playImv = (ImageView) findViewById(R.id.play_voice_imv);
        playTv = (TextView) findViewById(R.id.play_voice_tv);
        recordLayout = (RelativeLayout) findViewById(R.id.record_time_layout);
        recordImv = (ImageView) findViewById(R.id.record_voice_imv);
        recordTv = (TextView) findViewById(R.id.record_time_tv);

        topTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行上传录音文件等操作
                if (recorder.getTimeInterval() < 1) {
                    Toast.makeText(MyVoiceActivity.this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
                } else {
                    uploadVoice(recorder.getFilePath());
                }
            }
        });
        playImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    updatePauseView();
                } else {
                    updatePlayView();
                }
            }
        });
        recordImv.setOnTouchListener(new MyTouchListener());
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playHandler.removeCallbacks(playTask);
                playImv.setImageResource(R.drawable.mine_me_up_play_btn);
                playTv.setText("0S");
                currentTime = 0;
                isPlaying = false;
            }
        });

    }

    private void updatePlayView() {
        isPlaying = true;
        playTv.setText("0S");
        playImv.setImageResource(R.drawable.mine_me_up_play_btn);
        if (mPlayer != null) {
            audioStart();
        } else {
            mPlayer = new MediaPlayer();
            audioStart();
        }
        playHandler.post(playTask);
    }

    private void updatePauseView() {
        playHandler.removeCallbacks(playTask);
        isPlaying = false;
        playImv.setImageResource(R.drawable.mine_me_up_stop_btn);
        audioPause();
    }

    class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.record_voice_imv) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        updateVoiceViewStart();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateVoiceViewStop();
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    }

    private void updateVoiceViewStop() {
        recordLayout.setBackground(null);
        isHoldVoice = false;
        recordImv.setImageResource(R.drawable.mine_me_down_sound_record_btn_nor);
        recorder.stopRecording();
        recordHandler.removeCallbacks(recoreTask);
        audioPath = recorder.getFilePath();
    }

    private void updateVoiceViewStart() {
        recordLayout.setBackgroundResource(R.drawable.mine_me_down_time_img);
        recordTv.setText("00:00");
        isHoldVoice = true;
        recordTime = 0;
        recordImv.setImageResource(R.drawable.mine_me_down_sound_record_btn_clk);
        recorder.startRecording();
        recordHandler.post(recoreTask);
    }

    private void uploadVoice(String path) {
        showLoadingLayout("正在上传录音……");
        String endpoint = HttpContants.A_LI_YUN;
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("XWp6VLND94vZ8WNJ", "DSi9RRCv4bCmJQZOOlnEqCefW4l1eP");
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);
        String userCode = SharePreferanceUtils.getInstance().getUserCode(MyVoiceActivity.this, SharePreferanceUtils.USER_CODE, "");
        String fileName = "user/" + userCode + "/mine/voice/myVoice.acc";
        Log.d("mytest", "http://protect-app.oss-cn-beijing.aliyuncs.com/" + fileName);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("protect-app", fileName, path);

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
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                stopHandler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissLoadingLayout();
                Toast.makeText(MyVoiceActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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

    Runnable recoreTask = new Runnable() {
        @Override
        public void run() {
            recordTime++;
            if (recordTime <= 90) {
                if (recordTime < 10) {
                    recordTv.setText("00:0" + recordTime);
                } else {
                    recordTv.setText("00:" + recordTime);
                }
            } else {
                updateVoiceViewStop();
                return;
            }
            recordHandler.postDelayed(recoreTask, 1000);
        }
    };
    Runnable playTask = new Runnable() {
        @Override
        public void run() {
            currentTime++;
            playTv.setText(currentTime + "S");
            playHandler.postDelayed(playTask, 1000);
        }
    };

    public void audioStart() {
        Log.d("mytest", "-----" + audioPath);
        try {
            mPlayer.reset();
            //设置要播放的文件
            mPlayer.setDataSource(audioPath);
            mPlayer.prepare();
            //播放
            mPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void audioPause() {
        mPlayer.pause();
    }

    public void audioStop() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            audioStop();
        }
    }

    private void updateVoiceTime(int recordTime) {
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPDATE_USETINFO);
        String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, "");
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("device_code", deviceId);
            requestObject.put("voiceTimeNum", recordTime);
            requestObject.put("parameters", "voiceTimeNum");
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
                dismissLoadingLayout();
                LogUtil.d("mytest", "voice==" + result);
                try {
                    JSONObject jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        dismissLoadingLayout();
                        toastShort("上传失败");
                        return;
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

}
