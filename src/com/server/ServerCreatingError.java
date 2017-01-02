package com.server;

/**
 * Created by krego on 28.12.2016.
 */
public class ServerCreatingError extends RuntimeException{
    public ServerCreatingError(String message){
        super("Error creating server instnace: " + message);
    }
}
