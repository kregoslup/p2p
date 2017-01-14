package com.server;

/**
 * Created by krego on 06.11.2016.
 */
public enum RequestType {
    DIR(1),
    PULL(2),
    PUSH(3),
    ACK(4),
    HTML(5);

    private final int statusCode;

    RequestType(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getRequestTypeVerbose(){
        return name();
    }

    @Override
    public String toString(){
        return "" + statusCode;
    }
}
