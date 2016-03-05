package com.qtfreet.devasimpleapp.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.ui.App;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by qtfreet on 2016/3/5.
 */
public class WebViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    MaterialProgressBar progress;
    WebView webview;
    private SwipeRefreshLayout refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        App.getInstance().addActivity(WebViewActivity.this);
        initview();
    }

    private void initview() {


        String url = getIntent().getExtras().getString("url");
        webview = (WebView) findViewById(R.id.webview);
        progress = (MaterialProgressBar) findViewById(R.id.indeterminate_progress_library);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);


        refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R
                .color.holo_orange_light, android.R.color.holo_green_light);
        refresh.setOnRefreshListener(this);

        final FloatingActionButton f = (FloatingActionButton) findViewById(R.id.fab);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.this.finish();
            }
        });
        webview.loadUrl(url);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
                showRefreshing(false);

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
                return true;
            } else {
                Toast.makeText(WebViewActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRefresh() {
        webview.reload();
    }

    private void showRefreshing(boolean isShow) {
        if (isShow) {
            refresh.setProgressViewOffset(false, 0, (int) (getResources().getDisplayMetrics().density * 24 +
                    0.5f));
            refresh.setRefreshing(true);
        } else {
            refresh.setRefreshing(false);
        }
    }
}
