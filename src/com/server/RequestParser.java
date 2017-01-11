package com.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by krego on 07.11.2016.
 */
class RequestParser {
    private static final int chunkSize = RequestConfig.getInstance().getChunkSize();
    private ObjectMapper mapper;
    private HashMap<String, byte[]> filesMap;
    private RequestValidator requestValidator;
    private FileHandler fileHandler;

    RequestParser(HashMap<String, byte[]> filesMap, File downloadPath){
        mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.filesMap = filesMap;
        requestValidator = new RequestValidator(filesMap);
        this.fileHandler = new FileHandler(chunkSize, downloadPath);
    }

    Request parseIncomingRequest(InputStream inputStream)
            throws IOException, RequestParseException{
        Request request = mapper.readValue(inputStream, Request.class);
        validateRequest(request);
        return request;
    }

    private void validateRequest(Request request){
        if (request.getFileName() != null) {
            requestValidator.validateRequest(request, filesMap, chunkSize);
        }
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
            case MD5:
                response = new Request(RequestType.MD5, request.getFileName());
                response.setValid(requestValidator.checkMD5(request.getFileName(), request.getDataMD5()));
                break;
            case PULL:
                // ja czyli serwer wysylam
                fileName = fileHandler.getFullFileName(request.getFileName());
                System.out.println(fileName);
                response = new Request(RequestType.PULL, request.getDataSequence(), fileName);
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
