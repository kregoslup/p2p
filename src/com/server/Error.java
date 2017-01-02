package com.server;

/**
 * Created by krego on 28.12.2016.
 */
public class Error extends RuntimeException{
    public Error(String message){
        super(message);
    }
}
