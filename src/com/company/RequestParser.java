package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Created by krego on 07.11.2016.
 */
public class RequestParser {
    BufferedReader bufferedReader;
    private final char METHOD_TERMINATOR = '\0';

    RequestParser(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    RequestType getRequestType(){
        StringBuilder stringBuilder = new StringBuilder();
        readRequestPart(stringBuilder);
        String requestType = base64Decode(stringBuilder.toString());
        return RequestType.getRequestType(requestType);
    }

    private String getFilename(){
        StringBuilder stringBuilder = new StringBuilder();
        readRequestPart(stringBuilder);
        return base64Decode(stringBuilder.toString());
    }

    private void readRequestPart(StringBuilder stringBuilder){
        char c;
        try {
            while((c = (char)bufferedReader.read()) != METHOD_TERMINATOR){
                stringBuilder.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] base64Decode(String encodedRequestPart){
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(encodedRequestPart);
    }

    Request parseRequest(){
        RequestType requestType = this.getRequestType();
        return new Request(requestType, bufferedReader);
    }

    Response prepareResponse(Request request, ArrayList<String> filesMap, int currentPath, HostStatus status){
        Response response = new Response();
        switch(request.getRequestType()){
            case LIST:
                response.getFilesList(filesMap);
            case DOWNLOAD:
                response.sendFilePart(request);
            case MD5:
                response.countMD5(request);
            case ACCEPT:
                if(status == HostStatus.SENDING){
                    response.sendFilePart();
                }else{
                    break;
                }
        }
    }
}
