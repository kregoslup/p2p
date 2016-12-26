package com.company;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private static final int chunkSize = 102400;
    private ObjectMapper mapper;
    private HashMap<String, byte[]> filesMap;
    private RequestValidator requestValidator;

    RequestParser(HashMap<String, byte[]> filesMap){
        mapper = new ObjectMapper();
        this.filesMap = filesMap;
        requestValidator = new RequestValidator(filesMap);
    }

    Request parseIncomingRequest(BufferedReader bufferedReader)
            throws IOException, RequestParseException{
        Request request = this.mapper.readValue(bufferedReader, Request.class);
        requestValidator.validateRequest(request, filesMap, chunkSize);
        return request;
    }



    String parseOutgoingRequest(Request request,
                                int currentPart,
                                HostStatus status,
                                HashMap<String, byte[]> filesMap) throws RequestParseException{
        Request response = prepareResponseMethod(request, filesMap);
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RequestParseException();
        }
    }

    void setDataBase64Array(Request response){
        byte[] dataBase64Array = new byte[chunkSize];
        writeFilePart();
    }

    long calculateOffset(long partNumber, long chunkSize){

    }

    void writeFilePart(String fileName, ) throws FileNotFoundException {
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
