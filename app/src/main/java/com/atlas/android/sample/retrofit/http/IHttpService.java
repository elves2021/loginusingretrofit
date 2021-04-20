package com.atlas.android.sample.retrofit.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 请求接口
 */
public interface IHttpService {
    /**
     * 根据账号和密码进行登录
     *
     * @param userName 账号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("userName") String userName, @Field("password") String password);
}
