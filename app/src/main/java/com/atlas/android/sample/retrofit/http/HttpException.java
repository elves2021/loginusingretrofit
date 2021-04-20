package com.atlas.android.sample.retrofit.http;

/**
 * 网络通讯异常类
 */
public class HttpException extends Exception{
    public HttpException(){
        super();
    }

    public HttpException(String message) {
        super(message);
    }
}
