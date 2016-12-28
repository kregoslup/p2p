package com.server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by krego on 28.12.2016.
 */
public final class HostsController {
    static void startServers(ArrayList<Host> hosts, int portNumber){
        try {
            hosts.add(new Host(portNumber, hosts.size(), 1));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerCreatingError();
        }
    }
}
