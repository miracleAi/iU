package com.android.biubiu.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.biubiu.common.Constant;
import com.android.biubiu.component.record.AudioRecordLayout;
import com.android.biubiu.component.util.EmoticonUtil;
import com.android.biubiu.component.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;
import cc.imeetu.iu.timlibrary.presentation.viewfeatures.ChatView;

/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements TextWatcher, View.OnClickListener {

    private static final String TAG = "ChatInput";

    private ImageButton btnSend;
    private EditText editText;
    private boolean isHoldVoiceBtn, isEmoticonReady, isCollectReady;
    private InputMode inputMode = InputMode.NONE;
    private ChatView chatView;
    private AudioRecordLayout.onRecordStatusListener mRecordStatusListener;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    private LinearLayout mVoiceLayout, mPhotoLayout, mPicLayout, mEmoticonLayout;

    private RelativeLayout mRecordLayout;
    private AudioRecordLayout mRecordingLayout;

    private LinearLayout mAllEmoticonLayout;
    private int emojiSize;
    private int emojiLayoutSize;
    private final int ROWS = 3;
    private final int COLUMNS = 7;
    private int pages;
    private int emoticonLength;
    private ViewPager mPager;
    private ViewAdapter mAdapter;
    private LinearLayout mIndicator;

    private List<View> mPicViews = new ArrayList<>();

    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        emojiLayoutSize = Constant.screenWidth / 7;
        emojiSize = getResources().getDimensionPixelSize(R.dimen.emoji_size);
        emoticonLength = EmoticonUtil.emoticonData.length;
        LayoutInflater.from(context).inflate(R.layout.chat_input, this);
        initView();
    }

    private void initView() {
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });

        mVoiceLayout = (LinearLayout) findViewById(R.id.voice_layout);
        mPhotoLayout = (LinearLayout) findViewById(R.id.photo_layout);
        mPicLayout = (LinearLayout) findViewById(R.id.pic_layout);
        mEmoticonLayout = (LinearLayout) findViewById(R.id.emoticon_layout);

        mRecordLayout = (RelativeLayout) findViewById(R.id.recording_layout);
        mRecordingLayout = (AudioRecordLayout) mRecordLayout.findViewById(R.id.record_layout);

        mAllEmoticonLayout = (LinearLayout) findViewById(R.id.all_emoticon_layout);
        mVoiceLayout.setOnClickListener(this);
        mPhotoLayout.setOnClickListener(this);
        mPicLayout.setOnClickListener(this);
        mEmoticonLayout.setOnClickListener(this);

        mPager = (ViewPager) findViewById(R.id.emoticon_viewpager);
        mIndicator = (LinearLayout) findViewById(R.id.indicator_layout);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.emoji_layout).setOnClickListener(this);
        findViewById(R.id.collect_layout).setOnClickListener(this);
    }

    private void updateView(InputMode mode) {
        if (mode == inputMode) {
            if (inputMode == inputMode.TEXT) {
                return;
            }
            leavingCurrentState();
            inputMode = InputMode.NONE;
            return;
        }
        leavingCurrentState();
        switch (inputMode = mode) {
            case TEXT:
                if (editText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
                mRecordLayout.setVisibility(VISIBLE);
                break;
            case EMOTICON:
                if (!isEmoticonReady) {
                    prepareEmoticon();
                }
                mAllEmoticonLayout.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState() {
        switch (inputMode) {
            case TEXT:
                View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case VOICE:
                mRecordLayout.setVisibility(GONE);
                break;
            case EMOTICON:
                mAllEmoticonLayout.setVisibility(GONE);
                break;
        }
    }

    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public void setRecordStatusListener(AudioRecordLayout.onRecordStatusListener listener){
        mRecordStatusListener = listener;
        mRecordingLayout.setOnRecordStatusListener(mRecordStatusListener);
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        isSendVisible = s != null && s.length() > 0;
//        setSendBtn();
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

    }

    private void prepareEmoticon() {
        if (mPager == null) return;
        calculatePages();
        int pageNum = ROWS * COLUMNS;
        for (int i = 0; i < pages; i++) {//页数循环
            LinearLayout page = new LinearLayout(getContext());
            page.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            page.setOrientation(LinearLayout.VERTICAL);
            for (int j = 0; j < ROWS; j++) {//行数循环
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT, 1f);
                row.setLayoutParams(rowParams);
                for (int k = 0; k < COLUMNS; k++) {//列数循环
                    try {
                        AssetManager am = getContext().getAssets();
                        final int index = i * pageNum + COLUMNS * j + k;
                        if (index >= emoticonLength) {
                            break;
                        }
                        InputStream is = am.open(String.format("emoticon/%d.png", index));
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
//                    matrix.postScale(1.5f, 1.5f);
                        final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        ImageView image = new ImageView(getContext());
                        image.setImageBitmap(resizedBitmap);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(emojiSize, emojiSize);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        image.setLayoutParams(params);
                        image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String content = String.valueOf(index);
                                SpannableString str = new SpannableString(String.valueOf(index));
                                ImageSpan span = new ImageSpan(getContext(), resizedBitmap, ImageSpan.ALIGN_BASELINE);
                                str.setSpan(span, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                editText.append(str);
                            }
                        });
                        is.close();

                        RelativeLayout imgLayout = new RelativeLayout(getContext());
                        RelativeLayout.LayoutParams imgLytParam = new RelativeLayout.LayoutParams(emojiLayoutSize, emojiLayoutSize);
                        imgLayout.setLayoutParams(imgLytParam);

                        imgLayout.addView(image);

                        row.addView(imgLayout);
                    } catch (IOException e) {
                        System.out.println("aaa");
                    }
                }
                page.addView(row, j);
            }
            mPicViews.add(i, page);
        }
        mAdapter = new ViewAdapter(mPicViews);
        mPager.setAdapter(mAdapter);
        initIndicator();
        isEmoticonReady = true;
        setIndicator(0);
    }

    private void initIndicator() {
        if (mIndicator.getChildCount() > 0) {
            mIndicator.removeAllViews();
        }
        for (int i = 0; i < pages; i++) {
            View v = new View(getContext());
            v.setId(i);
            v.setBackgroundResource(R.drawable.indicator_default);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.indicator_width),
                    getResources().getDimensionPixelSize(R.dimen.indicator_height));
            if (i != 0) {
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.indicator_line_margin), 0, 0, 0);
            }
            mIndicator.addView(v, i, params);
        }
    }

    private void setIndicator(int id) {
        for (int i = 0; i < pages; i++) {
            if (i == id) {
                mIndicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_selected);
            } else {
                mIndicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_default);
            }
        }
    }

    private class ViewAdapter extends PagerAdapter {

        // 界面列表
        private List<View> views;

        public ViewAdapter(List<View> views) {
            this.views = views;
        }

        // 销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        // 获得当前界面数
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

    }

    private void calculatePages() {
        int pageNum = ROWS * COLUMNS;
        pages = emoticonLength / pageNum;
        if (emoticonLength % pageNum != 0) {
            pages += 1;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity activity = (Activity) getContext();
        switch (v.getId()) {
            case R.id.btn_send:
                chatView.sendText();
                break;
            case R.id.voice_layout:
                if (activity != null && requestAudio(activity)) {
                    updateView(InputMode.VOICE);
                }
                break;
            case R.id.photo_layout:
                if (activity != null && requestCamera(activity)) {
                    chatView.sendPhoto();
                }
                break;
            case R.id.pic_layout:
                if (activity != null && requestStorage(activity)) {
                    chatView.sendImage();
                }
                break;
            case R.id.emoticon_layout:
                updateView(InputMode.EMOTICON);
                break;
            case R.id.emoji_layout:

                break;
            case R.id.collect_layout:
                initCollect();
                break;
        }
    }

    private void initCollect() {
        if (!isCollectReady) {
            int collectSize = Constant.screenWidth / 4;
            int imgSize = getResources().getDimensionPixelSize(R.dimen.collect_size);
            int row = 2;
            int column = 4;
            int pageNum = row * column;
            int page;
            List<File> files = FileUtil.getCollectFiles();
            int size = files.size();
            size = 6;
            if (size > 0) {
                page = size / (pageNum);
                if (size % (row * column) != 0) {
                    page += 1;
                }
                for (int i = 0; i < page; i++) {
                    LinearLayout pageView = new LinearLayout(getContext());
                    pageView.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams pageParam = new LinearLayout.LayoutParams(-1, -1);
                    pageView.setLayoutParams(pageParam);
                    for (int j = 0; j < row; j++) {
                        LinearLayout rowView = new LinearLayout(getContext());
                        rowView.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(-1, -2, 1f);
                        rowView.setLayoutParams(rowParam);
                        for (int k = 0; k < column; k++) {
                            RelativeLayout columnView = new RelativeLayout(getContext());
                            RelativeLayout.LayoutParams columnParam = new RelativeLayout.LayoutParams(collectSize, collectSize);
                            columnView.setLayoutParams(columnParam);

                            ImageView imageView = new ImageView(getContext());
                            RelativeLayout.LayoutParams imgParam = new RelativeLayout.LayoutParams(imgSize, imgSize);
                            imgParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                            imageView.setLayoutParams(imgParam);
                            imageView.setBackgroundResource(R.drawable.main_imageview_picture4);
                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            columnView.addView(imageView);
                        }
                        pageView.addView(rowView, j);
                    }
                    mPicViews.add(pageView);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
        isCollectReady = true;
        mPager.setCurrentItem(pages);
    }

    /**
     * 获取输入框文字
     */
    public Editable getText() {
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text) {
        editText.setText(text);
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }


    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        MORE,// not used
        VIDEO,// not used
        NONE,
    }

    private boolean requestVideo(Activity activity) {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<String>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestCamera(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestAudio(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestStorage(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void releaseResource(){
        mRecordingLayout.release();

    }

}
