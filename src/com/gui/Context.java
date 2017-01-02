package com.gui;

import com.server.Host;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.12.2016.
 */
public class Context {
    private final static Context instance = new Context();
    static final int MAX_THREAD_POOL_SIZE_PER_HOST = 3;
    private static final int MAX_HOSTS = 10;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_HOSTS);

    static Context getInstance(){
        return instance;
    }

    private ObservableList<Host> hosts = FXCollections.observableArrayList();

    ObservableList<Host> getHosts(){
        return hosts;
    }

    void addHost(Host host){
        hosts.add(host);
        executorService.execute(host);
    }
}
