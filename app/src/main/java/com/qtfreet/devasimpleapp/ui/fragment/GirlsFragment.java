package com.qtfreet.devasimpleapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.data.bean.DataInfo;
import com.qtfreet.devasimpleapp.data.bean.ImageInfo;
import com.qtfreet.devasimpleapp.data.db.GankDB;
import com.qtfreet.devasimpleapp.data.net.Api;
import com.qtfreet.devasimpleapp.data.net.BearOkhttpUtils;
import com.qtfreet.devasimpleapp.ui.activity.DetailActivity;
import com.qtfreet.devasimpleapp.ui.adapter.GirlsAdapter;
import com.qtfreet.devasimpleapp.ui.adapter.OnMeiziClickListener;
import com.qtfreet.devasimpleapp.utils.SaveImageTask;
import com.qtfreet.devasimpleapp.wiget.ActionSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Bear on 2016/2/5.
 */
public class GirlsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMeiziClickListener {

    private static final int REQUEST_NUM = 5;
    private static final String REQUEST_URL = Api.GANK_API_GRILS;
    private static final int REQUEST_FAIL = 2;
    private static final int REQUEST_SUCCESS = 3;
    private static final int GET_URL_SUCCESS = 4;
    private static final int GET_SIZE_SUCCESS = 5;

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private Context mContext;
    private List<ImageInfo> imageInfos;
    private List<ImageInfo> imageCache;
    private boolean isLoadMore = false;
    private int hasLoadPage = 0;

    private GirlsAdapter mAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("state")) {
                case REQUEST_FAIL:
                    showRefreshing(false);
                    break;
                case GET_URL_SUCCESS:
                    showRefreshing(false);
                    List<ImageInfo> infos = new ArrayList<>();
                    for (ImageInfo info : imageCache) {
                        if (!db.contain(info)) {
                            infos.add(info);
                        } else {
                        }
                    }
                    dosometing(infos);
                    hasLoadPage++;
                    break;
                case GET_SIZE_SUCCESS:
                    Bundle data = msg.getData();
                    ImageInfo info = new ImageInfo();
                    info.setHeight(data.getInt("height"));
                    info.setWho(data.getString("who"));
                    info.setWidth(data.getInt("width"));
                    info.setUrl(data.getString("url"));
                    info.setTime(data.getString("time"));
                    db.saveImageInfo(info);
                    imageInfos.add(info);
                    mAdapter.notifyItemInserted(imageInfos.size());
                    isLoadMore = false;
                    break;
            }
        }
    };
    private GankDB db;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        db = GankDB.getInstance(mContext);

        imageInfos = db.findImageInfoAll();
        imageCache = new ArrayList<>();

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
        showRefreshing(true);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mAdapter = new GirlsAdapter(mContext, imageInfos);
        mAdapter.setOnMeiziClickListener(this);
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int[] positions = new int[mStaggeredGridLayoutManager.getSpanCount()];
                    mStaggeredGridLayoutManager.findLastVisibleItemPositions(positions);
                    for (int position : positions) {
                        if (position == mStaggeredGridLayoutManager.getItemCount() - 1) {
                            loadMore();
                            break;
                        }
                    }
                }


            }
        });

        requestData(1);
    }

    private void loadMore() {
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

    public void requestData(int page) {
        showRefreshing(true);
        BearOkhttpUtils.asynGet(REQUEST_URL + "/" + REQUEST_NUM + "/" + page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    DataInfo dataInfo = gson.fromJson(response.body().string(), DataInfo.class);
                    if (dataInfo == null || dataInfo.error) {
                        //error
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("state", REQUEST_FAIL);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        imageCache.clear();
                        for (DataInfo.ResultsEntity entity : dataInfo.results) {
                            ImageInfo info = new ImageInfo();
                            info.setUrl(entity.url);
                            info.setTime(entity.publishedAt);
                            info.setWho(entity.who);

                            imageCache.add(info);
                        }
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("state", GET_URL_SUCCESS);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        showRefreshing(false);
    }

    public Point loadImageForSize(String url) {
        Point point = new Point();
        try {
            Response response = BearOkhttpUtils.get(url);
            if (response.code() == 200) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(response.body().byteStream(), null, options);
                point.x = options.outWidth;
                point.y = options.outHeight;
                return point;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        point.x = 0;
        point.y = 0;
        return point;

    }

    public void dosometing(final List<ImageInfo> infos) {
        for (final ImageInfo info : infos) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Point point = loadImageForSize(info.getUrl());
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putInt("state", GET_SIZE_SUCCESS);
                    bundle.putString("url", info.getUrl());
                    bundle.putString("time", info.getTime());
                    bundle.putString("who", info.getWho());
                    bundle.putInt("width", point.x);
                    bundle.putInt("height", point.y);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }


    @Override
    public void onMeiziClick(View itemView, int position) {
        String url = imageInfos.get(position).getUrl();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onMeiziLongClick(View itemView, final int position) {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true).addSheetItem(mContext.getString(R.string.save_image), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {

                String time = imageInfos.get(position).getTime();
                String url = imageInfos.get(position).getUrl();
                SaveImageTask saveImageUtils = new SaveImageTask(getActivity(), time);
                saveImageUtils.execute(url);


            }
        }).addSheetItem(mContext.getString(R.string.share_image), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {

            }
        }).show();
    }

}
