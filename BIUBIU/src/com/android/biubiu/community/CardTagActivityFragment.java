package com.android.biubiu.community;

import android.app.Fragment;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.biubiu.bean.TagBean;
import com.android.biubiu.callback.HttpCallback;
import com.android.biubiu.common.Constant;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.HttpRequestUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardTagActivityFragment extends Fragment {
    private View rootView;
    private RelativeLayout backRl;
    private EditText searchEt;
    private LinearLayout searchTagLayout;
    private LinearLayout createTagLayout;
    private LinearLayout recommendLayout;
    private LinearLayout hotLayout;
    private LinearLayout newLayout;
    private ListView searchLv;
    private ListView recommendLv;
    private ListView hotLv;

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
        return rootView;
    }

    public void getInfo() {
        Bundle b = getArguments();
        toTagType = b.getString(Constant.TO_TAG_TYPE);
    }

    private void initView() {
        backRl = (RelativeLayout) rootView.findViewById(R.id.back_rl);
        searchEt = (EditText) rootView.findViewById(R.id.search_tag_et);
        searchTagLayout = (LinearLayout) rootView.findViewById(R.id.search_tag_layout);
        searchTagLayout.setVisibility(View.GONE);
        createTagLayout = (LinearLayout) rootView.findViewById(R.id.create_tag_layout);
        recommendLayout = (LinearLayout) rootView.findViewById(R.id.recommend_tag_layout);
        hotLayout = (LinearLayout) rootView.findViewById(R.id.hot_tag_layout);
        newLayout = (LinearLayout) rootView.findViewById(R.id.new_tag_layout);
        searchLv = (ListView) rootView.findViewById(R.id.search_tag_lv);
        recommendLv = (ListView) rootView.findViewById(R.id.recommend_tag_lv);
        hotLv = (ListView) rootView.findViewById(R.id.hot_tag_lv);

        searchAdapter = new TagAdapter(getActivity(), searchList);
        recomendAdapter = new TagAdapter(getActivity(), recommendLsit);
        hotAdapter = new TagAdapter(getActivity(), hotList);
        newAdapter = new TagAdapter(getActivity(), newList);

        recommendLv.setAdapter(recomendAdapter);
        hotLv.setAdapter(hotAdapter);

        recommendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    // TODO: 2016/5/31 发布跳转点击在此处理
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理 
                }

            }
        });
        hotLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    // TODO: 2016/5/31 发布跳转点击在此处理
                } else {
                    // TODO: 2016/5/31 社区跳转点击在此处理
                }
            }
        });

        searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toTagType.equals(Constant.TAG_TYPE_PUBLISH)) {
                    // TODO: 2016/5/31 发布跳转点击在此处理
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
                    searchTagLayout.setVisibility(View.VISIBLE);
                    queryCount = queryCount + 1;
                    getTagByKeyword(s.toString(), queryCount);
                } else {
                    searchTagLayout.setVisibility(View.GONE);
                }
            }
        });
        createTagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    try {
                        hasNext = object.getInt("hasNext");
                        postNum = object.getInt("postNum");
                        JSONArray recommendArray = object.getJSONArray("recommend");
                        JSONArray hotArray = object.getJSONArray("hot");
                        JSONArray newArray = object.getJSONArray("newrest");
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
                            hotList.addAll(list1);
                            hotAdapter.notifyDataSetChanged();
                        }
                        if (list3 != null && list3.size() > 0) {
                            if (isRefresh) {
                                newList.clear();
                            }
                            newList.addAll(list1);
                            newAdapter.notifyDataSetChanged();
                        }
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
                if(object != null){
                    try {
                        TagBean bean = new TagBean();
                        int tagId = object.getInt("tagId");
                        bean.setContent(tagStr);
                        bean.setId(tagId);
                        // TODO: 2016/5/31 创建标签成功后的操作 
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
