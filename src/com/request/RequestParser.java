package com.request;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.util.HashMap;

/**
 * Created by krego on 07.11.2016.
 */
public class RequestParser extends BaseRequestParser {

    public RequestParser(File downloadPath){
        super(downloadPath);
    }

    public String parseOutgoingRequest(Request request,
                                       HashMap<String, byte[]> filesMap) throws RequestParseException{
        Request response = prepareResponseMethod(request, filesMap);
        return convertResponseToString(response);
    }

    private String convertResponseToString(Request response){
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RequestParseException("Error parsing requet class to string");
        }
    }

    private Request prepareResponseMethod(Request request, HashMap<String, byte[]> filesMap) {
        Request response;
        String fileName;
        switch (request.getRequestType()){
            case DIR:
                response = new Request(RequestType.DIR, filesMap);
                break;
            case PULL:
                // ja czyli serwer wysylam
                fileName = fileHandler.getFullFileName(request.getFileName());
                response = new Request(RequestType.PULL, request.getDataSequence(), request.getFileName());
                response.setDataBase64Array(fileHandler.loadFilePart(fileName, request.getDataSequence()));
                response.setMaxDataSequence(fileHandler.calculateFileParts(new File(fileName)));
                break;
            case PUSH:
                // ktos mi wysyla, ja sciagam, ja serwer
                fileName = fileHandler.getFullFileName(request.getFileName());
                fileHandler.writeFilePart(fileName, request.getDataBase64Array(), request.getDataSequence());
                response = new Request(RequestType.ACK, request.getDataSequence(), request.getFileName());
                break;
            default:
                throw new RequestParseException("Error preparing response");
        }
        return response;
    }
}
