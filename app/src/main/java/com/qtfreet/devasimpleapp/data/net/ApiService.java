package com.qtfreet.devasimpleapp.data.net;

import com.qtfreet.devasimpleapp.data.bean.DataInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by qtfreet on 2016/3/30.
 */
public interface ApiService {
    @GET("api/data/福利/{count}/{page}")
    Call<DataInfo> Data(@Path("count") String count, @Path("page") int page);

    @GET("api/data/{type}/{count}/{page}")
    Call<DataInfo> DataText(@Path("type") String type, @Path("count") String count, @Path("page") int page);

}
