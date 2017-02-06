package com.request;

/**
 * Created by krego on 30.12.2016.
 */
public class RequestConfig {
    private final static RequestConfig config = new RequestConfig();
    private static int chunkSize = 102400;

    public int getChunkSize(){
        return chunkSize;
    }

    public static RequestConfig getInstance(){
        return config;
    }
}
