package com.qtfreet.devasimpleapp.data.net;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Bear on 2016/2/3.
 */
public class httpUtils {

    public static Response get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response res = client.newCall(request).execute();
        return res;
    }
}
