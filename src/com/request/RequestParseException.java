package com.request;

/**
 * Created by krego on 26.12.2016.
 */
public class RequestParseException extends RuntimeException{
    public RequestParseException(String message){
        super("Error parsing request: " + message);
    }
}
