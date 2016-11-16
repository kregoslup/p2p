package com.company;

import java.io.BufferedReader;
import java.util.Optional;

/**
 * Created by krego on 05.11.2016.
 */
class Request {
    private RequestType requestType;
    private BufferedReader bufferedReader = null;

    Request(RequestType requestType, BufferedReader bufferedReader){
        this.requestType = requestType;
        this.bufferedReader = bufferedReader;
    }

    RequestType getRequestType() {
        return requestType;
    }

    Optional getBufferedReader(){
        return Optional.ofNullable(bufferedReader);
    }

}
