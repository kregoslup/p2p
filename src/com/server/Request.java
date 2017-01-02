package com.server;

import java.util.HashMap;

/**
 * Created by krego on 05.11.2016.
 */
class Request {
    public RequestType requestType;
    public HashMap<String, byte[]> filesMap = null;
    public byte[] dataBase64Array = null;
    public long dataSequence = 0;
    public long maxDataSequence = 0;
    public String fileName = null;
    public byte[] dataMD5 = null;
    public boolean valid = true;

    Request(RequestType requestType){
        this.requestType = requestType;
    }

    Request(RequestType requestType, HashMap<String, byte[]> filesMap){
        this.requestType = requestType;
        this.filesMap = filesMap;
    }

    Request(RequestType requestType, String fileName){
        this.requestType = requestType;
        this.fileName = fileName;
    }

    Request(RequestType requestType, long dataSequence, String fileName){
        this.requestType = requestType;
        this.dataSequence = dataSequence;
        this.fileName = fileName;
    }

    Request(RequestType requestType, long dataSequence, long maxDataSequence, String fileName, byte[] dataBase64Array){
        this.requestType = requestType;
        this.dataSequence = dataSequence;
        this.fileName = fileName;
        this.dataBase64Array = dataBase64Array;
        this.maxDataSequence = maxDataSequence;
    }

    public void setValid(boolean valid){
        this.valid = valid;
    }

    public byte[] getDataMD5(){
        return dataMD5;
    }

    public byte[] getDataBase64Array(){
        return dataBase64Array;
    }

    public RequestType getRequestType(){
        return this.requestType;
    }

    public void setDataMD5(byte[] dataMD5){
        this.dataMD5 = dataMD5;
    }

    public String getFileName(){
        return this.fileName;
    }

    public long getDataSequence(){
        return dataSequence;
    }

    public HashMap<String, byte[]> getFilesMap() {
        return filesMap;
    }

    public boolean isValid() {
        return valid;
    }

    public long getMaxDataSequence() {
        return maxDataSequence;
    }
}
