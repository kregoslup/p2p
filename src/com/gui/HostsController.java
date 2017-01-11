package com.gui;

import com.server.Host;
import javafx.collections.ObservableList;

import java.util.concurrent.ExecutorService;

/**
 * Created by krego on 28.12.2016.
 */
class HostsController {

    static void closeHost(ExecutorService executorService){
        executorService.shutdown();
    }
}
