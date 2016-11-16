package com.company;

/**
 * Created by krego on 06.11.2016.
 */
public enum RequestType {
    LIST("LST"), GET("GET"), MD5("MD5"), ACCEPT("ACK"), DATA("DATA");

    private String socketVal;

    private RequestType(String socketVal){
        this.socketVal = socketVal;
    }

    public static RequestType getRequestType(String socketVal){
        socketVal = socketVal.toUpperCase();
        for(RequestType shortVal : RequestType.values()){
            if(shortVal.getSocketVal().equals(socketVal)){
                return shortVal;
            }
        }
        throw new IllegalArgumentException(socketVal);
    }

    public String getSocketVal(){
        return socketVal;
    }
}
