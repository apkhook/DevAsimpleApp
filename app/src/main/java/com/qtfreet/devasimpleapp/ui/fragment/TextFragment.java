package com.qtfreet.devasimpleapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.ui.activity.WebViewActivity;
import com.qtfreet.devasimpleapp.ui.adapter.ContentAdapter;
import com.qtfreet.devasimpleapp.data.bean.ContentItemInfo;
import com.qtfreet.devasimpleapp.data.bean.DataInfo;
import com.qtfreet.devasimpleapp.data.net.Api;
import com.qtfreet.devasimpleapp.data.net.BearOkhttpUtils;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Bear on 2016/1/29.
 */
public class TextFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;


    public static final int TYPE_ANDROID = 0;
    public static final int TYPE_IOS = 1;
    private static final int REQUEST_FAIL = 2;
    private static final int REQUEST_SUCCESS = 3;
    private static final int REQUEST_NUM = 20;

    private int hasLoadPage = 0;
    private boolean isLoadMore = false;

    private String requestUrl = Api.GANK_API_ADROID;

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private List<ContentItemInfo> itemInfos;
    private ContentAdapter mAdapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("state")) {
                case REQUEST_FAIL:
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    if (isLoadMore) {
                        isLoadMore = false;
                    }
                    break;
                case REQUEST_SUCCESS:
                    if (handleJson(msg.getData().getString("json"))) {
                        hasLoadPage++;
                        if (hasLoadPage == 1) {
                            sp.edit().putString("json", msg.getData().getString("json")).apply();
                        }
                    }
                    break;
            }
            showRefreshing(false);
        }
    };
    private SharedPreferences sp;

    public static TextFragment newFragment(int flag) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", flag);
        TextFragment textFragment = new TextFragment();
        textFragment.setArguments(bundle);
        return textFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);

        int type = getArguments().getInt("type", TYPE_ANDROID);
        if (type == TYPE_ANDROID) {
            requestUrl = Api.GANK_API_ADROID;
        } else if (type == TYPE_IOS) {
            requestUrl = Api.GANK_API_IOS;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_layout, container, false);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R
                .color.holo_orange_light, android.R.color.holo_green_light);
        refresh.setOnRefreshListener(this);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastItem + 1 == mAdapter.getItemCount())) {
                    LoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        itemInfos = new ArrayList<>();

        mAdapter = new ContentAdapter(getActivity(), itemInfos);
        recyclerView.setAdapter(mAdapter);

        requestData(1);

        mAdapter.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = itemInfos.get(position).getUrl();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });

    }


    private void LoadMore() {
        if (!isLoadMore) {
            isLoadMore = true;
            requestData(hasLoadPage + 1);
        }

    }

    private void showRefreshing(boolean isShow) {
        if (isShow) {
            refresh.setProgressViewOffset(false, 0, (int) (mContext.getResources().getDisplayMetrics().density * 24 +
                    0.5f));
            refresh.setRefreshing(true);
        } else {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        hasLoadPage = 0;
        isLoadMore = false;
        requestData(1);
    }


    public void requestData(int page) {
        showRefreshing(true);
        BearOkhttpUtils.asynGet(requestUrl + "/" + REQUEST_NUM + "/" + page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putInt("state", REQUEST_FAIL);
                Message msg = Message.obtain();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putInt("state", REQUEST_SUCCESS);
                bundle.putString("json", response.body().string());
                Message msg = Message.obtain();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
    }


    private boolean handleJson(String json) {
        Gson gson = new Gson();
        DataInfo dataInfo = gson.fromJson(json, DataInfo.class);

        if (dataInfo == null || dataInfo.error) {
            Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
            if (isLoadMore) {
                isLoadMore = false;
            }
            return false;
        } else {
            List<DataInfo.ResultsEntity> results = dataInfo.results;
//            itemInfos.clear();
            List<ContentItemInfo> list = new ArrayList<>();
            for (DataInfo.ResultsEntity entity : results) {
                ContentItemInfo info = new ContentItemInfo();
                info.setContent(entity.desc);
                info.setUrl(entity.url);
                info.setWho(entity.who);
                info.setTime(entity.publishedAt);

//                itemInfos.add(info);
                list.add(info);
            }
            if (isLoadMore) {
                itemInfos.addAll(list);
                isLoadMore = false;
            } else {
                itemInfos.clear();
                itemInfos.addAll(list);
            }
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }


}
