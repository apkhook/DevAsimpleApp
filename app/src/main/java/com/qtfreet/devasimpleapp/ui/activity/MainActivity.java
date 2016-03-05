package com.qtfreet.devasimpleapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.ui.App;
import com.qtfreet.devasimpleapp.ui.adapter.FragmentAdapter;
import com.qtfreet.devasimpleapp.ui.fragment.GirlsFragment;
import com.qtfreet.devasimpleapp.ui.fragment.TextFragment;
import com.qtfreet.devasimpleapp.update.CheckUpdate;
import com.qtfreet.devasimpleapp.utils.Utils;
import com.qtfreet.devasimpleapp.wiget.ActionSheetDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.tab)
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        App.getInstance().addActivity(MainActivity.this);
    }

    private void initData() {

        List<String> titles = new ArrayList<>();
        titles.add("福利");
        titles.add("Android");
        titles.add("iOS");


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new GirlsFragment());
        fragments.add(TextFragment.newFragment(TextFragment.TYPE_ANDROID));
        fragments.add(TextFragment.newFragment(TextFragment.TYPE_IOS));


        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments, titles));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarTitle.setText("首页");
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView header = (ImageView) headerView.findViewById(R.id.header_view);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setTitle("更换头像")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false).addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Toast.makeText(MainActivity.this, "该功能并没有实现=。=", Toast.LENGTH_SHORT).show();
                    }
                }).addSheetItem("从图库选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Toast.makeText(MainActivity.this, "该功能并没有实现=。=", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login) {
            startActivity(LoginActivity.class);
            return true;
        } else if (id == R.id.exit) {
            App.getInstance().exit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);

    }


    private void startActivity(Class c) {
        Intent i = new Intent(MainActivity.this, c);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //捕获返回键按下的事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.showDialog(this, "确定要退出了吗?");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.setting) {
            Toast.makeText(MainActivity.this, "该功能并没有实现=。=", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_check) {
            CheckUpdate.getInstance().startCheck(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
