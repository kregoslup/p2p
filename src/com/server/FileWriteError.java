package com.server;

/**
 * Created by krego on 28.12.2016.
 */
class FileWriteError extends RuntimeException{
    FileWriteError(String message){
        super(message);
    }
}
