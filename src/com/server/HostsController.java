package com.server;

import javafx.collections.ObservableList;

/**
 * Created by krego on 28.12.2016.
 */
public class HostsController {

    public static void closeAllHosts(ObservableList<Host> hosts){
        if (!hosts.isEmpty()) {
            hosts.forEach(Host::abort);
        }
    }
}
