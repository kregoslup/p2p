package com.gui;

import com.server.Host;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, byte[]> currentFiles;
    private ArrayList<ServerListController> serverList = new ArrayList<>();

    private Context(){
        hosts.addListener((ListChangeListener<? super Host>) c -> {
            serverList.forEach(ServerListController::refresh);
        });
    }

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

    void addServerList(ServerListController controller){
        serverList.add(controller);
    }

    HashMap<String, byte[]> getCurrentFiles() {
        return currentFiles;
    }

    void setCurrentFiles(HashMap<String, byte[]> files) {
        currentFiles = files;
    }
}
