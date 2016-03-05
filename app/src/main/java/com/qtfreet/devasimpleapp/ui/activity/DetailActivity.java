package com.qtfreet.devasimpleapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.ui.App;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qtfreet on 2016/3/5.
 */
public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.iv_meizi)
    ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail);
        App.getInstance().addActivity(DetailActivity.this);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        String url = getIntent().getExtras().getString("url");
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return super.onTouchEvent(event);
    }
}
