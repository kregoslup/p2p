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
                                HashMap<String, byte[]> filesMap) throws RequestParseException{
        Request response = prepareResponseMethod(request, filesMap);
        return convertResponseToString(response);
    }

    private String convertResponseToString(Request response){
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RequestParseException();
        }
    }

    private void writeFilePart(Request request) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(request.getFileName()));
            long offset = request.getDataSequence() * chunkSize;
            int read = bis.read(request.getDataBase64Array(), (int)offset, chunkSize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestParseException();
        }
    }

    private boolean checkMD5(String fileName, byte[] dataMD5) {
        return filesMap.containsKey(fileName) && filesMap.get(fileName) == dataMD5;
    }

    Request prepareResponseMethod(Request request, HashMap<String, byte[]> filesMap) {
        Request response;
        switch (request.getRequestType()){
            case DIR:
                response = new Request(RequestType.DIR, filesMap);
                break;
            case MD5:
                response = new Request(RequestType.MD5, request.getFileName());
                response.setValid(checkMD5(request.getFileName(), request.getDataMD5()));
                break;
            case PULL:
                response = new Request(RequestType.PULL, request.getDataSequence(), request.getFileName());
                writeFilePart(response);
                break;
            case PUSH:
                response = new Request(RequestType.PUSH, request.getDataSequence(), request.getFileName());
                break;
            default:
                throw new RequestParseException();
        }
        return response;
    }
}
