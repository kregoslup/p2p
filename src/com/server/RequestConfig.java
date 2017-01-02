package com.server;

/**
 * Created by krego on 30.12.2016.
 */
class RequestConfig {
    private final static RequestConfig config = new RequestConfig();
    private static int chunkSize = 102400;

    int getChunkSize(){
        return chunkSize;
    }

    static RequestConfig getInstance(){
        return config;
    }
}
