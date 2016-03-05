package com.qtfreet.devasimpleapp.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.utils.SMSContentObserver;
import com.qtfreet.devasimpleapp.utils.Utils;
import com.qtfreet.devasimpleapp.wiget.PhoneNumWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

import static com.mob.tools.log.MobUncaughtExceptionHandler.register;

/**
 * Created by qtfreet00 on 2016/3/4.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.code)
    EditText code;
    @Bind(R.id.getcode)
    Button btn_getcode;
    @Bind(R.id.verify)
    Button btn_verify;
    SMSContentObserver mObserver;
    boolean isSendMsg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        SMSSDK.initSDK(LoginActivity.this, "100d3c9bd0f5b", "0184f76be2d1d797b9b1db75ff6f2351");
        SMSSDK.registerEventHandler(eventHandler);
        initview();
    }

    private void initview() {
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarTitle.setText("用户登录");
            }
        }
        btn_getcode.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        phone.addTextChangedListener(new PhoneNumWatcher(11, phone, btn_getcode));
//        code.addTextChangedListener(new PhoneNumWatcher(6, code));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getcode:

                mObserver = new SMSContentObserver(this, new Handler(), code);
                // 注册短信变化监听
                getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mObserver);
                if (!Utils.isMobilePhone(phone.getText().toString())) {
                    return;
                }
                SMSSDK.getVerificationCode("86", phone.getText().toString().trim());

                break;
            case R.id.verify:
                if (isSendMsg) {
                   SMSSDK.submitVerificationCode("86", phone.getText().toString().trim(), code.getText().toString().trim());
                }
                break;
        }
    }

    EventHandler eventHandler = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result != SMSSDK.RESULT_COMPLETE) {
                Log.v("cc", "连接短信验证码服务器失败");
                ((Throwable) data).printStackTrace();
                return;
            }
            //回调完成
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //验证码验证成功，服务器的反馈事件
                //实例化短信监听器
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "验证成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.v("cc", "验证成功");
                register();
            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                //手机请求验证码，服务器在发送验证码之后，给与的网络反馈
                isSendMsg = true;
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "已经发送验证码", Toast.LENGTH_SHORT).show();
                Log.v("cc", "已经发送验证码");
                Looper.loop();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mObserver != null) {
            getContentResolver().unregisterContentObserver(mObserver);
        }
    }
}

