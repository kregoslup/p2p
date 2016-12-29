package com.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;

/**
 * Created by krego on 07.11.2016.
 */
class RequestParser {
    private static final int chunkSize = 102400;
    private ObjectMapper mapper;
    private HashMap<String, byte[]> filesMap;
    private RequestValidator requestValidator;
    private FileHandler fileHandler;

    RequestParser(HashMap<String, byte[]> filesMap){
        mapper = new ObjectMapper();
        this.filesMap = filesMap;
        requestValidator = new RequestValidator(filesMap);
        this.fileHandler = new FileHandler(chunkSize);
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

    Request prepareResponseMethod(Request request, HashMap<String, byte[]> filesMap) {
        Request response;
        switch (request.getRequestType()){
            case DIR:
                response = new Request(RequestType.DIR, filesMap);
                break;
            case MD5:
                response = new Request(RequestType.MD5, request.getFileName());
                response.setValid(requestValidator.checkMD5(request.getFileName(), request.getDataMD5()));
                break;
            case PULL:
                // ja czyli serwer wysylam
                response = new Request(RequestType.PULL, request.getDataSequence(), request.getFileName());
                response.setDataMD5(fileHandler.loadFilePart(request.getFileName(), request.getDataSequence()));
                break;
            case PUSH:
                // ktos mi wysyla, ja sciagam, ja serwer
                fileHandler.writeFilePart(request.getFileName(), request.getDataBase64Array(), request.getDataSequence());
                response = new Request(RequestType.ACK, request.getDataSequence(), request.getFileName());
                break;
            default:
                throw new RequestParseException();
        }
        return response;
    }
}
