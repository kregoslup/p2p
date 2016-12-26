package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by krego on 05.11.2016.
 */
class Request {
    private RequestType requestType;
    private HashMap<String, byte[]> filesMap = null;
    private byte[] dataBase64Array = null;
    private long dataSequence = 0;
    private String fileName = null;
    private String dataMD5 = null;
    private boolean valid = true;

    Request(){
        valid = false;
    }

    Request(RequestType requestType, HashMap<String, byte[]> filesMap){
        this.requestType = requestType;
        this.filesMap = filesMap;
    }

    Request(RequestType requestType, String fileName){
        this.requestType = requestType;
        this.fileName = fileName;
    }

    Request(RequestType requestType, long dataSequence, String fileName) throws RequestParseException{
        this.requestType = requestType;
        this.dataSequence = dataSequence;
        this.fileName = fileName;
    }

    RequestType getRequestType(){
        return this.requestType;
    }


    void setFilesMap(HashMap<String, byte[]> filesMap){
        this.filesMap = filesMap;
    }

    void setFileName(String fileName){
        this.fileName = fileName;
    }

    void setDataMD5(String dataMD5){
        this.dataMD5 = dataMD5;
    }

    String getFileName(){
        return this.fileName;
    }

    long getDataSequence(){
        return dataSequence;
    }
}
