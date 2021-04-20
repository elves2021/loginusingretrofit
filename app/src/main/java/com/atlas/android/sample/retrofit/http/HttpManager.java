package com.atlas.android.sample.retrofit.http;

import android.util.Log;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 网络请求类
 * <p>
 * 单例-双重校验锁方式
 */
public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();

    // 请求连接超时，默认设置10秒
    private static final int CONNECT_TIMEOUT = 10000;
    // 写入超时，默认设置10秒
    private static final int WRITE_TIMEOUT = 10000;
    // 读取超时，默认设置10秒
    private static final int READ_TIMEOUT = 10000;

    // 测试请求地址，使用https请求
    private String mBaseUrl = "https://utst77.natappfree.cc/api/";

    private volatile static HttpManager sInstance;

    private HttpManager() {
    }

    public static HttpManager getInstance() {
        if (sInstance == null) {
            synchronized (HttpManager.class) {
                if (sInstance == null) {
                    sInstance = new HttpManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * 自定义SSLSocket, 忽略验证客户端和服务端证书。
     * @return
     * @throws Exception
     */
    private static SSLSocketFactory getSSLSocketFactory() throws Exception {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // 信任客户端证书
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // 信任服务端证书
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    /**
     * 创建retrofit实例
     *
     * @return
     */
    private Retrofit createRetrofit() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(getSSLSocketFactory())
                // 信任手机所有CA证书
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    /**
     * 请求登录
     *
     * @return true 登录成功,false登录失败
     * @throws HttpException
     */
    public boolean doLogin(String userName, String password) throws HttpException {
        try {
            Retrofit retrofit = createRetrofit();
            IHttpService httpService = retrofit.create(IHttpService.class);
            Call<ResponseBody> call = httpService.login(userName, password);
            Response<ResponseBody> repo = call.execute();

            if (repo == null) {
                return false;
            }

            Log.d(TAG, "Login resp code:" + repo.code());
            return repo.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(e.getMessage());
        }
    }
}
