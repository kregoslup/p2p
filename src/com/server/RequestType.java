package com.server;

/**
 * Created by krego on 06.11.2016.
 */
public enum RequestType {
    DIR(1),
    MD5(2),
    PULL(3),
    PUSH(4),
    ACK(5),
    HTML(6);

    private final int statusCode;

    RequestType(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getRequestTypeVerbose(RequestType type){
        return type.name();
    }

    @Override
    public String toString(){
        return "" + statusCode;
    }
}
