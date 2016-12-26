package com.company;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by krego on 07.11.2016.
 */
class RequestParser {
    static final int chunkSize = 102400;
    private ObjectMapper mapper;

    RequestParser(){
        mapper = new ObjectMapper();
    }

/*    String prepareResponse(BufferedReader bufferedReader) throws IOException{
        Request request = parseJSONRequest(bufferedReader);
        System.out.println(request);
        return request.getRequestType().toString();
    }*/

    Request parseIncomingRequest(BufferedReader bufferedReader, HashMap<String, byte[]> filesMap)
            throws IOException, RequestParseException{
        Request request = this.mapper.readValue(bufferedReader, Request.class);
        validateRequest(request, filesMap);
    }

    private void validateRequest(Request request, HashMap<String, byte[]> filesMap) throws RequestParseException{
        validateDataSequence(request.getDataSequence(), request.getFileName());
        validateFile(request.getFileName(request.getFileName(), filesMap));
    }

    String parseOutgoingRequest(Request request,
                                int currentPart,
                                HostStatus status,
                                HashMap<String, byte[]> filesMap) throws RequestParseException{
        Request response = prepareResponseMethod(request, filesMap);
    }

    private void validateFile(String fileName, HashMap<String, byte[]> filesMap) throws RequestParseException{
        if (!filesMap.containsKey(fileName)){
            throw new RequestParseException();
        }
    }

    private void validateDataSequence(long dataSequence, String fileName) throws RequestParseException{
        long size = new File(fileName).length();
        long availableParts = size / chunkSize;
        if (dataSequence > availableParts){
            throw new RequestParseException();
        }
    }

    void setDataBase64Array(){
        dataBase64Array = new byte[chunkSize];
        writeFilePart();
    }

    void writeFilePart() throws FileNotFoundException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
        long offset = calculateOffset();
        bis.read(dataBase64Array, offset, chunkSize);
    }

    Request prepareResponseMethod(Request request, HashMap<String, byte[]> filesMap) throws RequestParseException{
        Request response;
        switch (request.getRequestType()){
            case DIR:
                response = new Request(RequestType.DIR, filesMap);
                break;
            case MD5:
                response = new Request(RequestType.MD5, request.getFileName());
                break;
            case DATA:
                response = new Request(RequestType.DATA, request.getDataSequence(), request.getFileName());
                break;
            default:
                response = new Request();
        }
        return response;
    }
}
