package com.gui;

import com.server.Client;
import com.server.Host;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by krego on 29.12.2016.
 */
public class Context {
    private final static Context instance = new Context();
    static final int MAX_THREAD_POOL_SIZE_PER_HOST = 3;

    static Context getInstance(){
        return instance;
    }

    private ObservableList<Host> hosts = FXCollections.observableArrayList();

    private Client client = new Client();

    ObservableList<Host> getHosts(){
        return hosts;
    }

    void addHost(Host host){
        hosts.add(host);
    }

    Client getClient(){
        return client;
    }
}
