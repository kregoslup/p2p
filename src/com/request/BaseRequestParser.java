package com.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.FileHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by krego on 06.02.2017.
 */
abstract class BaseRequestParser implements ResponseParser{
    private static final int chunkSize = RequestConfig.getInstance().getChunkSize();
    ObjectMapper mapper;
    FileHandler fileHandler;

    BaseRequestParser(File downloadPath){
        mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.fileHandler = new FileHandler(chunkSize, downloadPath);
    }

    public Request parseIncomingRequest(InputStream inputStream)
            throws IOException, RequestParseException{
        try {
            return mapper.readValue(inputStream, Request.class);
        }catch (JsonParseException e) {
            return new Request(RequestType.HTML);
        }catch (JsonMappingException e){
            throw new RequestParseException("Error parsing request");
        }
    }

    public ObjectMapper getMapper(){
        return this.mapper;
    }

}
