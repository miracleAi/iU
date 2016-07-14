package com.android.biubiu.ui.mine.child;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.ui.base.BaseActivity;

import cc.imeetu.iu.R;

public class MyVoiceActivity extends BaseActivity {

    private ImageView playImv;
    private TextView playTv;
    private RelativeLayout recordLayout;
    private TextView recordTv;
    private ImageView recordImv;
    private TopTitleView topTitle;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voice);
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
            }
        });
        playImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    isPlaying = false;
                    playImv.setImageResource(R.drawable.mine_me_up_play_btn);
                }else{
                    isPlaying = true;
                    playImv.setImageResource(R.drawable.mine_me_up_stop_btn);
                }
            }
        });
        recordImv.setOnTouchListener(new MyTouchListener());
    }

    class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getId() == R.id.record_voice_imv){

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    recordImv.setImageResource(R.drawable.mine_me_down_sound_record_btn_clk);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    recordImv.setImageResource(R.drawable.mine_me_down_sound_record_btn_nor);
                }
            }
            return false;
        }
    }
}
