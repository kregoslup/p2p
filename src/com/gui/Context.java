package com.gui;

import com.server.Host;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.12.2016.
 */
class Context {
    private final static Context instance = new Context();
    static final int MAX_THREAD_POOL_SIZE_PER_HOST = 3;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private HashMap<String, byte[]> currentFiles;

    static Context getInstance(){
        return instance;
    }

    void addHost(Host host){
        executorService.execute(host);
    }

    HashMap<String, byte[]> getCurrentFiles() {
        return currentFiles;
    }

    void setCurrentFiles(HashMap<String, byte[]> files) {
        currentFiles = files;
    }

    ExecutorService getExecutor() {
        return executorService;
    }
}
