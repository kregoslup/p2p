package com.server;

import java.nio.file.Path;

/**
 * Created by krego on 30.12.2016.
 */
class RequestConfig {
    private final static RequestConfig config = new RequestConfig();
    private static int chunkSize = 102400;
    private Path downloadPath;

    int getChunkSize(){
        return chunkSize;
    }

    static RequestConfig getInstance(){
        return config;
    }

    Path getDownloadPath(){
        return downloadPath;
    }

    void setDownloadPath(Path downloadPath){
        this.downloadPath = downloadPath;
    }

}
