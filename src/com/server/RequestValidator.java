package com.server;

import java.io.File;
import java.util.HashMap;

/**
 * Created by krego on 26.12.2016.
 */
class RequestValidator {
    private HashMap<String, byte[]> filesMap;

    RequestValidator(HashMap<String, byte[]> filesMap){
        this.filesMap = filesMap;
    }

    void validateRequest(Request request, HashMap<String, byte[]> filesMap, long chunkSize)
            throws RequestParseException{
        validateDataSequence(request.getDataSequence(), request.getFileName(), chunkSize);
        if (request.getRequestType() == RequestType.PULL) {
            validateFile(request.getFileName(), filesMap);
        }
    }

    private void validateFile(String fileName, HashMap<String, byte[]> filesMap) throws RequestParseException{
        if (!filesMap.containsKey(fileName)){
            throw new RequestParseException("Invalid requested file");
        }
    }

    private void validateDataSequence(long dataSequence, String fileName, long chunkSize)
            throws RequestParseException{
        long size = new File(fileName).length();
        long availableParts = size / chunkSize;
        if (dataSequence > availableParts){
            throw new RequestParseException("Invalid file part number");
        }
    }

    boolean checkMD5(String fileName, byte[] dataMD5) {
        return filesMap.containsKey(fileName) && filesMap.get(fileName) == dataMD5;
    }
}
