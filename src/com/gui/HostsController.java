package com.gui;

import com.server.Host;
import javafx.collections.ObservableList;

/**
 * Created by krego on 28.12.2016.
 */
class HostsController {

    static void closeAllHosts(ObservableList<Host> hosts){
        if (!hosts.isEmpty()) {
            hosts.forEach(Host::abort);
        }
    }
}
