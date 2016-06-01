package com.android.biubiu.community;

import android.app.Fragment;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.bean.TagBean;
import com.android.biubiu.callback.HttpCallback;
import com.android.biubiu.common.Constant;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.HttpRequestUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardTagActivityFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private View rootView;
    private LinearLayout allTagLayout;
    private RelativeLayout backRl;
    private EditText searchEt;
    private LinearLayout searchTagLayout;
    private LinearLayout createTagLayout;
    private ListView searchLv;
    private ListView recommendLv;
    private ListView hotLv;
    private TextView countTv;
    private Button cancelBtn;
    private View headerView;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;

    private long lastTime = 0;
    private long postNum = 0;
    private int hasNext = 0;

    private ArrayList<TagBean> recommendLsit = new ArrayList<TagBean>();
    private ArrayList<TagBean> hotList = new ArrayList<TagBean>();
    private ArrayList<TagBean> newList = new ArrayList<TagBean>();
    private ArrayList<TagBean> searchList = new ArrayList<TagBean>();

    private TagAdapter recomendAdapter;
    private TagAdapter hotAdapter;
    private TagAdapter newAdapter;
    private TagAdapter searchAdapter;

    private int itemHeight = 49;

    private String toTagType = "";
    //模糊查询次数，为避免前一次查询比后一次慢
    private int queryCount = 0;

    public CardTagActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_card_tag, container, false);
        getInfo();
        initView();
        getTagList(true);
        return rootView;
    }

    public void getInfo() {
        Bundle b = getArguments();
        if(b != null && null != b.getString(Constant.TO_TAG_TYPE)){
            toTagType = b.getString(Constant.TO_TAG_TYPE);
        }else{
            toTagType = "";
        }
    }

    private void initView() {
        backRl = (RelativeLayout) rootView.findViewById(R.id.back_rl);
        allTagLayout = (LinearLayout) rootView.findViewById(R.id.all_tag_layout);
        allTagLayout.setVisibility(View.GONE);
        searchEt = (EditText) rootView.findViewById(R.id.search_tag_et);
        countTv = (TextView) rootView.findViewById(R.id.count_tv);
        cancelBtn = (Button) rootView.findViewById(R.id.cancel_btn);
        searchTagLayout = (LinearLayout) rootView.findViewById(R.id.search_tag_layout);
        searchTagLayout.setVisibility(View.GONE);
        createTagLayout = (LinearLayout) rootView.findViewById(R.id.create_tag_layout);
        searchLv = (ListView) rootView.findViewById(R.id.search_tag_lv);
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.tag_head_view,null);
        recommendLv = (ListView) headerView.findViewById(R.id.recommend_tag_lv);
        hotLv = (ListView) headerView.findViewById(R.id.hot_tag_lv);
        mPullToRefreshListview = (PullToRefreshListView) rootView.findViewById(R.id.pull_refresh_list);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();
        mListview.addHeaderView(headerView);

        searchAdapter = new TagAdapter(getActivity(), searchList);
        recomendAdapter = new TagAdapter(getActivity(), recommendLsit);
        hotAdapter = new TagAdapter(getActivity(), hotList);
        newAdapter = new TagAdapter(getActivity(), newList);

        recommendLv.setAdapter(recomendAdapter);
        hotLv.setAdapter(hotAdapter);
        mListview.setAdapter(newAdapter);

        recommendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    Intent intent = new Intent();
                    intent.putExtra("tagBean", (Serializable) recommendLsit.get(position));
                    getActivity().setResult(getActivity().RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理 
                }

            }
        });
        hotLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    Intent intent = new Intent();
                    intent.putExtra("tagBean", (Serializable) hotList.get(position));
                    getActivity().setResult(getActivity().RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理
                }
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    Intent intent = new Intent();
                    intent.putExtra("tagBean", (Serializable) newList.get(position-mListview.getHeaderViewsCount()));
                    getActivity().setResult(getActivity().RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理
                }
            }
        });

        searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    Intent intent = new Intent();
                    intent.putExtra("tagBean", (Serializable) searchList.get(position));
                    getActivity().setResult(getActivity().RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理
                }
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    countTv.setText(""+(30-s.length()));
                    searchTagLayout.setVisibility(View.VISIBLE);
                    queryCount = queryCount + 1;
                    getTagByKeyword(s.toString(), queryCount);
                } else {
                    searchTagLayout.setVisibility(View.GONE);
                    createTagLayout.setVisibility(View.GONE);
                }
            }
        });
        createTagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEt.getText() != null && searchEt.getText().toString().length() > 0) {
                    createTag(searchEt.getText().toString());
                }
            }
        });
        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryCount = 0;
                searchTagLayout.setVisibility(View.GONE);
                createTagLayout.setVisibility(View.GONE);
                searchEt.setText("");
            }
        });
    }

    //获取关键字匹配列表
    private void getTagByKeyword(final String keyword, int count) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("searchStr", keyword);
            requestObject.put("num", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestUtils.commonRequest(getActivity(), requestObject, HttpContants.GET_TAGS_BY_NAME, new HttpCallback() {
            @Override
            public void callback(JSONObject object, String error) {
                if (object != null) {
                    try {
                        JSONArray searchArray = object.getJSONArray("tags");
                        Gson gson = new Gson();
                        ArrayList<TagBean> list = gson.fromJson(searchArray.toString(), new TypeToken<List<TagBean>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            if (!list.get(0).equals(keyword)) {
                                createTagLayout.setVisibility(View.VISIBLE);
                            } else {
                                createTagLayout.setVisibility(View.GONE);
                            }
                            searchList.clear();
                            searchList.addAll(list);
                            searchAdapter.notifyDataSetChanged();
                        }else{
                            createTagLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //获取标签列表
    private void getTagList(final boolean isRefresh) {
        JSONObject requestObject = new JSONObject();
        try {
            if (isRefresh) {
                requestObject.put("time", 0);
                requestObject.put("postNum", 0);
            } else {
                requestObject.put("time", lastTime);
                requestObject.put("postNum", postNum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestUtils.commonRequest(getActivity(), requestObject, HttpContants.GET_TAGS, new HttpCallback() {
            @Override
            public void callback(JSONObject object, String error) {
                if (object != null) {
                    allTagLayout.setVisibility(View.VISIBLE);
                    try {
                        hasNext = object.getInt("hasNext");
                        postNum = object.getInt("postNum");
                        if(postNum < queryCount){
                            return;
                        }
                        JSONArray recommendArray = object.optJSONArray("recommend");
                        JSONArray hotArray = object.optJSONArray("hot");
                        JSONArray newArray = object.optJSONArray("new");
                        Gson gson = new Gson();
                        ArrayList<TagBean> list1 = gson.fromJson(recommendArray.toString(), new TypeToken<List<TagBean>>() {
                        }.getType());
                        ArrayList<TagBean> list2 = gson.fromJson(hotArray.toString(), new TypeToken<List<TagBean>>() {
                        }.getType());
                        ArrayList<TagBean> list3 = gson.fromJson(newArray.toString(), new TypeToken<List<TagBean>>() {
                        }.getType());
                        if (list1 != null && list1.size() > 0) {
                            recommendLsit.clear();
                            recommendLsit.addAll(list1);
                            recomendAdapter.notifyDataSetChanged();
                        }
                        if (list2 != null && list2.size() > 0) {
                            hotList.clear();
                            hotList.addAll(list2);
                            hotAdapter.notifyDataSetChanged();
                        }
                        if (list3 != null && list3.size() > 0) {
                            if (isRefresh) {
                                newList.clear();
                            }
                            newList.addAll(list3);
                            newAdapter.notifyDataSetChanged();
                        }
                        initSize();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), "请求标签列表失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //创建标签
    private void createTag(final String tagStr) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("content", tagStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestUtils.commonRequest(getActivity(), requestObject, HttpContants.CREATE_TAG, new HttpCallback() {
            @Override
            public void callback(JSONObject object, String error) {
                stopLoad();
                if (object != null) {
                    try {
                        TagBean bean = new TagBean();
                        int tagId = object.getInt("tagId");
                        bean.setContent(tagStr);
                        bean.setId(tagId);
                        Intent intent = new Intent();
                        intent.putExtra("tagBean", (Serializable) bean);
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void stopLoad() {
        if (mPullToRefreshListview.isRefreshing()) {
            mPullToRefreshListview.onRefreshComplete();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (hasNext == Constants.HAS_NO_DATA) {
            Toast.makeText(getActivity(), "已经到底了", Toast.LENGTH_SHORT).show();
            stopLoad();
        } else {
            getTagList(false);
        }
    }

    private void initSize() {
// 动态设置listview 高度
        LinearLayout.LayoutParams recommentLinear = (LinearLayout.LayoutParams)recommendLv.getLayoutParams();
        recommentLinear.height = DensityUtil.dip2px(getActivity(), itemHeight) * recommendLsit.size();
        recommendLv.setLayoutParams(recommentLinear);

        LinearLayout.LayoutParams hotLinear = (LinearLayout.LayoutParams)hotLv.getLayoutParams();
        hotLinear.height = DensityUtil.dip2px(getActivity(), itemHeight) * hotList.size();
        hotLv.setLayoutParams(hotLinear);

       /* LinearLayout.LayoutParams newLinear = (LinearLayout.LayoutParams)mPullToRefreshListview.getLayoutParams();
        newLinear.height = DensityUtil.dip2px(getActivity(), itemHeight) * newList.size();
        mPullToRefreshListview.setLayoutParams(newLinear);*/
    }
}
