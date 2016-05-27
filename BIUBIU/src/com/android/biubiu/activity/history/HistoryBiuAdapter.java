package com.android.biubiu.activity.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biubiu.bean.HistoryBiuBean;
import com.android.biubiu.bean.Schools;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.DateUtils;

import org.xutils.x;

import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/5/16.
 */
public class HistoryBiuAdapter extends BaseAdapter {
    private List<HistoryBiuBean> mData;
    private LayoutInflater mInflater;
    private Context mCon;
    private SchoolDao mSchoolDao;

    public HistoryBiuAdapter(Context con, List<HistoryBiuBean> data) {
        this.mData = data;
        mInflater = LayoutInflater.from(con);
        mCon = con;
        mSchoolDao = new SchoolDao();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryBiuBean biuBean = mData.get(position);
        BiuBeanHolder beanHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.history_biu_item_layout, parent, false);
            beanHolder = new BiuBeanHolder(convertView);
            convertView.setTag(beanHolder);
        } else {
            beanHolder = (BiuBeanHolder) convertView.getTag();
        }
        if (biuBean != null) {
            beanHolder.match.setText(biuBean.getMatchingScore() + "%");
            beanHolder.biuTime.setText(DateUtils.getDateFormatInList(mCon, biuBean.getTime()));
            beanHolder.biuDistance.setText(mCon.getResources().getString(R.string.biu_distance,
                    CommonUtils.formatDistance(biuBean.getDistance())));
            beanHolder.nickname.setText(biuBean.getNickname());
            x.image().bind(beanHolder.headImg, biuBean.getUserHead());
            List<Schools> schoolses = mSchoolDao.getschoolName(biuBean.getSchool());
            if (schoolses != null && schoolses.size() > 0) {
                beanHolder.schoolAge.setText(mCon.getResources().getString(R.string.school_age,
                        schoolses.get(0).getUnivsNameString(), biuBean.getAge()));
            }else{
                beanHolder.schoolAge.setText(mCon.getResources().getString(R.string.school_age,
                        "", biuBean.getAge()));
            }
            beanHolder.sexImg.setImageResource("1".equals(biuBean.getSex()) ? R.drawable.biu_ago_icon_sex_boy :
                    R.drawable.biu_ago_icon_sex_girl);
            beanHolder.tag.setText(biuBean.getChatTags());
        }
        return convertView;
    }

    private class BiuBeanHolder {
        private TextView match, biuTime, biuDistance, nickname, schoolAge, tag;
        private ImageView headImg, sexImg;

        public BiuBeanHolder(View view) {
            match = (TextView) view.findViewById(R.id.compatibility_textview);
            biuTime = (TextView) view.findViewById(R.id.time_textview);
            biuDistance = (TextView) view.findViewById(R.id.distance_textview);
            nickname = (TextView) view.findViewById(R.id.nickname_textview);
            schoolAge = (TextView) view.findViewById(R.id.school_age_textview);
            tag = (TextView) view.findViewById(R.id.tag_textview);

            headImg = (ImageView) view.findViewById(R.id.head_imageview);
            sexImg = (ImageView) view.findViewById(R.id.sex_imageview);

        }
    }
}
