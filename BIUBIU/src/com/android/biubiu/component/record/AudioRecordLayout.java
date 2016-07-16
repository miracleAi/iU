package com.android.biubiu.component.record;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.imeetu.iu.R;


/**
 * MyRelativeLayout[v 1.0.0]
 * classes:com.lly.rxjavaretrofitdemo.view.MyRelativeLayout
 *
 * @author lileiyi
 * @date 2016/3/25
 * @time 10:43
 * @description 语音功能
 */
public class AudioRecordLayout extends RelativeLayout implements View.OnClickListener {

    private static final int UPDATETIME = 0;

    /**
     * 录制结果接口
     */
    private onRecordStatusListener mOnRecordStatusListener;
    /**
     * 松开试听按钮
     */
    private AuditionButton mAuditionBtn;
    /**
     * 删除语音按钮
     */
    private AuditionButton mDeleteVoiceBtn;
    /**
     * 录制状态文字显示
     */
    private TextView tv_hintText;
    /**
     * 录制时间显示
     */
    private TextView tv_time;
    /**
     * 录制按钮
     */
    private ImageView imgbtn_record;
    /**
     * 录制抖动效果显示的View
     */
    private FluctuateView mFluctuateView1;
    private FluctuateView mFluctuateView2;
    /**
     * 用户当前按下的X坐标
     */
    private float currenX;
    /**
     * 用户当前按钮下的Y坐标
     */
    private float currenY;

    /**
     * 试听按钮中心点的X坐标
     */
    private float auditionX;
    /**
     * 试听按钮中心点的X坐标
     */
    private float auditionY;
    /**
     * 删除按钮中心带你的X坐标
     */
    private float deleteX;
    /**
     * 删除按钮中心带你的Y坐标
     */
    private float deleteY;
    /**
     * 试听按钮和录制按钮之间的距离
     */
    private double distance1;
    /**
     * 删除按钮和录制按钮之间的距离
     */
    private double distance2;
    /**
     * 删除按钮的宽度
     */
    private int delteBtnWidth;
    /**
     * 删除按钮的高度
     */
    private int delteBtnHeight;
    /**
     * 试听按钮的宽度
     */
    private int auditionBtnWidth;
    /**
     * 试听按钮的高度
     */
    private int auditionBtnHeight;

    //判断第一次进来
    private boolean isflag = true;

    private LinearLayout mCancelSendLyt;
    private TextView mCancelTv, mSendTv;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                mOnRecordStatusListener.onRecordCancle();
                cancleAudition();
                break;
            case R.id.send_btn:
                mOnRecordStatusListener.onRecordComplete();
                cancleAudition();
                break;
        }
    }

    private void cancleAudition() {
        Status = RecordStatus.CANCLE_RECORD;
        updateButton();
        mCancelSendLyt.setVisibility(GONE);
        tv_time.setVisibility(GONE);
        tv_hintText.setVisibility(VISIBLE);
        tv_hintText.setText("按住说话");
        resetTime();
    }

    /**
     * 录制状态
     */
    public enum RecordStatus {
        PREPARE_RECOIRD,
        STARTE_RECORD,
        CANCLE_RECORD,
        AUDITION_RECORD//试听
    }

    /**
     * 默认是准备录制状态
     */
    private RecordStatus Status = RecordStatus.PREPARE_RECOIRD;

    /**
     * 当前录制的时间
     */
    private int currentTime;
    /**
     * 更新时间
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATETIME) {
                if (Status == RecordStatus.STARTE_RECORD) {
                    currentTime += 1000;
                    tv_time.setText(showTimeCount(currentTime));
                    mHandler.sendEmptyMessageDelayed(UPDATETIME, 1000);
                }
            }
        }
    };

    /**
     * 设置监听器
     *
     * @param mOnRecordStatusListener
     */
    public void setOnRecordStatusListener(onRecordStatusListener mOnRecordStatusListener) {
        this.mOnRecordStatusListener = mOnRecordStatusListener;
    }

    public AudioRecordLayout(Context context) {
        super(context);
    }

    public AudioRecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioRecordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取播放View
        mAuditionBtn = (AuditionButton) getChildAt(1);
        mDeleteVoiceBtn = (AuditionButton) getChildAt(2);
        tv_hintText = (TextView) findViewById(R.id.tv_hint);
        imgbtn_record = (ImageView) findViewById(R.id.imgbtn_record);
        mFluctuateView1 = (FluctuateView) findViewById(R.id.fluctuate_view1);
        mFluctuateView2 = (FluctuateView) findViewById(R.id.fluctuate_view2);
        tv_time = (TextView) findViewById(R.id.tv_time);
        mCancelSendLyt = (LinearLayout) findViewById(R.id.cancle_send_layout);
        mCancelTv = (TextView) findViewById(R.id.cancel_btn);
        mSendTv = (TextView) findViewById(R.id.send_btn);
        mCancelTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        onMeasureWidthAndHeight();

    }


    /**
     * 测量各控件宽度和高度
     */
    public void onMeasureWidthAndHeight() {
        //获取删除按钮宽和高
        ViewTreeObserver vto = mDeleteVoiceBtn.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDeleteVoiceBtn.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                delteBtnWidth = mDeleteVoiceBtn.getWidth();
                delteBtnHeight = mDeleteVoiceBtn.getHeight();
            }
        });
        //获取试听按钮的宽和高
        ViewTreeObserver vto1 = mAuditionBtn.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAuditionBtn.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                auditionBtnWidth = mAuditionBtn.getWidth();
                auditionBtnHeight = mAuditionBtn.getHeight();
            }
        });
    }


    /**
     * 获取各按钮中心点的坐标
     */
    public void onCenterCoordinates() {
        //获取试听按钮中心点的坐标
        auditionX = mAuditionBtn.getX() + auditionBtnWidth / 2;
        auditionY = mAuditionBtn.getY() + auditionBtnHeight / 2;
        //获取删除按钮中心点的坐标
        deleteX = mDeleteVoiceBtn.getX() + delteBtnWidth / 2;
        deleteY = mDeleteVoiceBtn.getY() + delteBtnHeight / 2;
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currenX = event.getX();
        currenY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //判断当前按下的坐标是否在按钮范围里面
                if (containRecordBtn()) {
                    //开始录制
                    Status = RecordStatus.STARTE_RECORD;
                    updateButton();
                    tv_time.setText("00:00");
                    mHandler.sendEmptyMessageDelayed(UPDATETIME, 1000);
                    if (mOnRecordStatusListener != null) {
                        mOnRecordStatusListener.onRecordStart();
                    }
                    if (isflag) {
                        onCenterCoordinates();
                        distance1 = getCoordinateDistance(auditionX, auditionY, currenX, currenY);
                        distance2 = getCoordinateDistance(currenX, currenY, deleteX, deleteY);
                        isflag = false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Status == RecordStatus.STARTE_RECORD) {
                    //松开
                    if (containDeleteView()) {
                        tv_hintText.setVisibility(VISIBLE);
                        hideTimeLayout();
                        tv_hintText.setText("松开取消发送");
                    } else if (containPlayView()) {
                        tv_hintText.setVisibility(VISIBLE);
                        hideTimeLayout();
                        tv_hintText.setText("松开试听");
                    } else {
                        tv_hintText.setVisibility(GONE);
                        showTimeLayout();
                        tv_time.setText(showTimeCount(currentTime));
                    }
                    double distance = getCoordinateDistance(auditionX, auditionY, currenX, currenY);
                    mAuditionBtn.setDistance(distance / distance1);

                    double distance1 = getCoordinateDistance(currenX, currenY, deleteX, deleteY);
                    mDeleteVoiceBtn.setDistance(distance1 / distance2);
                }
                break;
            case MotionEvent.ACTION_UP:
                Status = RecordStatus.CANCLE_RECORD;
                mHandler.removeMessages(UPDATETIME);
                if (containDeleteView()) {
                    if (mOnRecordStatusListener != null) {
                        mOnRecordStatusListener.onRecordCancle();
                    }
                    resetTime();
                } else if (containPlayView()) {
                    Status = RecordStatus.AUDITION_RECORD;
                } else {
                    if (mOnRecordStatusListener != null) {
                        mOnRecordStatusListener.onRecordComplete();
                    }
                    resetTime();
                }
                updateButton();
                mAuditionBtn.setSelected(false);
                mDeleteVoiceBtn.setSelected(false);
        }
        return true;
    }

    /**
     * 隐藏录制时间布局
     */
    public void hideTimeLayout() {
        tv_time.setVisibility(GONE);
        mFluctuateView1.setVisibility(GONE);
        mFluctuateView2.setVisibility(GONE);
    }


    /**
     * 显示录制时间布局
     */
    public void showTimeLayout() {
        tv_time.setVisibility(VISIBLE);
        mFluctuateView1.setVisibility(VISIBLE);
        mFluctuateView2.setVisibility(VISIBLE);
    }


    /**
     * 松开按钮重置时间
     */
    public void resetTime() {
//        mHandler.removeMessages(UPDATETIME);
        tv_time.setText("00:00");
        currentTime = 0;
    }

    /**
     * 更新显示
     */
    public void updateButton() {
        if (Status == RecordStatus.STARTE_RECORD) {
            mAuditionBtn.setVisibility(VISIBLE);
            mDeleteVoiceBtn.setVisibility(VISIBLE);
            //波动效果
            showTimeLayout();
            mFluctuateView1.start();
            mFluctuateView2.start();
            tv_hintText.setVisibility(GONE);
        } else if (Status == RecordStatus.AUDITION_RECORD) {
            tv_hintText.setVisibility(GONE);
            tv_time.setVisibility(VISIBLE);
            tv_time.setText(showTimeCount(currentTime));
            //试听功能
            mAuditionBtn.setDistance(1f);
            mAuditionBtn.setVisibility(GONE);
            //删除功能
            mDeleteVoiceBtn.setDistance(1f);
            mDeleteVoiceBtn.setVisibility(GONE);
            //波动效果
            mFluctuateView1.stop();
            mFluctuateView2.stop();

            mFluctuateView1.setVisibility(GONE);
            mFluctuateView2.setVisibility(GONE);

            mCancelSendLyt.setVisibility(VISIBLE);
        } else {
            tv_hintText.setVisibility(VISIBLE);
            tv_hintText.setText("按住说话");
            //试听功能
            mAuditionBtn.setDistance(1f);
            mAuditionBtn.setVisibility(GONE);
            //删除功能
            mDeleteVoiceBtn.setDistance(1f);
            mDeleteVoiceBtn.setVisibility(GONE);
            //波动效果
            mFluctuateView1.stop();
            mFluctuateView2.stop();
            hideTimeLayout();
        }
    }

    /**
     * 获取二个坐标点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public double getCoordinateDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 判断当前当前按住的点是否在录制按钮里面
     *
     * @return
     */
    public boolean containRecordBtn() {
        if ((imgbtn_record.getX() < currenX && (imgbtn_record.getX() + imgbtn_record.getWidth()) > currenX)
                && (imgbtn_record.getY() < currenY && (imgbtn_record.getY() + imgbtn_record.getHeight()) > currenY)) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前滑动范围是否在试听按钮里面
     */
    public boolean containPlayView() {
        if ((mAuditionBtn.getX() < currenX && (mAuditionBtn.getX() + mAuditionBtn.getWidth()) > currenX)
                && (mAuditionBtn.getY() < currenY && (mAuditionBtn.getY() + mAuditionBtn.getHeight()) > currenY)) {
            tv_hintText.setText("松手试听");
            mAuditionBtn.setSelected(true);
            return true;
        }
        mAuditionBtn.setSelected(false);
        return false;
    }


    /**
     * 判断当前滑动范围是否在删除里面
     */
    public boolean containDeleteView() {
        if ((mDeleteVoiceBtn.getX() < currenX && (mDeleteVoiceBtn.getX() + mDeleteVoiceBtn.getWidth()) > currenX)
                && (mDeleteVoiceBtn.getY() < currenY && (mDeleteVoiceBtn.getY() + mDeleteVoiceBtn.getHeight()) > currenY)) {
            mDeleteVoiceBtn.setSelected(true);
            return true;
        }
        mDeleteVoiceBtn.setSelected(false);
        return false;
    }


    /**
     * 转换时间
     *
     * @param time
     * @return
     */
    public String showTimeCount(long time) {
        if (time >= 360000000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600000) / (60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());

        long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());
        timeCount = minue + ":" + sec;
        return timeCount;
    }

    /**
     * 录制状态监听器
     */
    public interface onRecordStatusListener {

        /**
         * 录制开始
         */
        void onRecordStart();

        /**
         * 录制完成
         */
        void onRecordComplete();

        /**
         * 录制取消
         */
        void onRecordCancle();

    }
}
