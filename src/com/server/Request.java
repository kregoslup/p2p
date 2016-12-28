package com.server;

import java.util.HashMap;

/**
 * Created by krego on 05.11.2016.
 */
class Request {
    private RequestType requestType;
    private HashMap<String, byte[]> filesMap = null;
    private byte[] dataBase64Array = null;
    private int dataSequence = 0;
    private String fileName = null;
    private byte[] dataMD5 = null;
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

    Request(RequestType requestType, int dataSequence, String fileName) throws RequestParseException{
        this.requestType = requestType;
        this.dataSequence = dataSequence;
        this.fileName = fileName;
    }

    void setValid(boolean valid){
        this.valid = valid;
    }

    byte[] getDataMD5(){
        return dataMD5;
    }

    byte[] getDataBase64Array(){
        return dataBase64Array;
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

    void setDataMD5(byte[] dataMD5){
        this.dataMD5 = dataMD5;
    }

    String getFileName(){
        return this.fileName;
    }

    int getDataSequence(){
        return dataSequence;
    }
}
