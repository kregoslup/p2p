package com.server;

/**
 * Created by krego on 28.12.2016.
 */
public class FileWriteError extends RuntimeException{
    public FileWriteError(String message){
        super(message);
    }
}
