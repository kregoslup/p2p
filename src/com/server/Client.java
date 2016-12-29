package com.server;

import java.io.File;

/**
 * Created by krego on 28.12.2016.
 */
public class Client {
    private Host clientHost;
    private File downloadPath;

    Client(Host clientHost, String clientPath){
        this.clientHost = clientHost;
        configureDownloadPath();
    }

    void download
}
