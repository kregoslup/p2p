package com.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.HashMap;

/**
 * Created by krego on 05.11.2016.
 */
class Request {
    private RequestType requestType;
    private HashMap<String, byte[]> filesMap = null;
    private byte[] dataBase64Array = null;
    private long dataSequence = 0;
    private long maxDataSequence = 0;
    private String fileName = null;
    private byte[] dataMD5 = null;
    private boolean valid = true;

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

    @JsonCreator
    Request(@JsonProperty("requestType") RequestType requestType,
            @JsonProperty("filesMap") HashMap<String, byte[]> filesMap,
            @JsonProperty("dataSequence") long dataSequence,
            @JsonProperty("maxDataSequence") long maxDataSequence,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("dataBase64Array") byte[] dataBase64Array){
        this.requestType = requestType;
        this.dataSequence = dataSequence;
        this.fileName = fileName;
        this.dataBase64Array = dataBase64Array;
        this.maxDataSequence = maxDataSequence;
        this.filesMap = filesMap;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setFilesMap(HashMap<String, byte[]> filesMap) {
        this.filesMap = filesMap;
    }

    public void setDataBase64Array(byte[] dataBase64Array) {
        this.dataBase64Array = dataBase64Array;
    }

    public void setDataSequence(long dataSequence) {
        this.dataSequence = dataSequence;
    }

    public void setMaxDataSequence(long maxDataSequence) {
        this.maxDataSequence = maxDataSequence;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
