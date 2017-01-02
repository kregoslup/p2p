package com.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by krego on 28.12.2016.
 */
public class Client implements Runnable{
    private RequestType clientActionType;
    private int portNumber;
    private Socket socket;
    private FileHandler fileHandler;
    private static final int chunkSize = RequestConfig.getInstance().getChunkSize();
    private String fileName;
    private ObjectMapper mapper;
    public HashMap<String, byte[]> filesMap;
    private static final int FILE_FIRST_PART = 0;

    public Client(RequestType clientActionType, int portNumber, String fileName){
        this.clientActionType = clientActionType;
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.mapper = new ObjectMapper();
        setUpSocket();
        addHandlers();
    }

    public Client(RequestType clientActionType, int portNumber){
        this.clientActionType = clientActionType;
        this.portNumber = portNumber;
        this.mapper = new ObjectMapper();
        setUpSocket();
        addHandlers();
    }

    private void addHandlers(){
        this.fileHandler = new FileHandler();
    }

    private void setUpSocket(){
        try {
            socket = new Socket(InetAddress.getByName("localhost"), portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Error setting up socket in client");
        }
    }

    private void downloadFile(){
        Request request = new Request(RequestType.PULL, Client.FILE_FIRST_PART, fileName);
        sendRequest(request);
        Request response = parseIncomingRequest();
        fileHandler.writeFilePart(fileName, response.getDataBase64Array(), 0);
        long fileParts = response.getMaxDataSequence();
        for (int i = 1; i<fileParts; i++){
            sendRequest(new Request(RequestType.PULL, i, fileName));
            Request loopResponse = parseIncomingRequest();
            fileHandler.writeFilePart(fileName, loopResponse.getDataBase64Array(), 0);
        }
    }

    private void uploadFile(){
        long fileParts = fileHandler.calculateFileParts(new File(fileName), chunkSize);
        for (long i = 0; i < fileParts; i++){
            byte[] dataArray = fileHandler.loadFilePart(fileName, i);
            sendRequest(new Request(
                    RequestType.PUSH, i, fileParts, fileName, dataArray
            ));
            Request request = parseIncomingRequest();
            if (request.getRequestType() != RequestType.ACK){
                break;
            }
        }
    }

    private String convertRequestToString(Request request){
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RequestParseException("Error parsing request class to string");
        }
    }

    private void sendRequest(Request response){
        String jsonRequest = this.convertRequestToString(response);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            outputStreamWriter.write(jsonRequest);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Got an error while sending message from client");
        }
    }

    private Request parseIncomingRequest(){
        try {
            BufferedInputStream bufferedReader = new BufferedInputStream(socket.getInputStream());
            return this.mapper.readValue(bufferedReader, Request.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestParseException("Error parsing host request");
        }
    }

    private void getFiles(){
        Request request = new Request(RequestType.DIR);
        sendRequest(request);
        System.out.println(request);
        Request response = parseIncomingRequest();
        filesMap = response.getFilesMap();
    }

    private boolean checkMD5(){
        Request request = new Request(RequestType.MD5, fileName);
        try {
            request.setDataMD5(FileHandler.countMD5(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendRequest(request);
        Request response = parseIncomingRequest();
        return response.isValid();
    }

    private void parseGUICommand(){
        switch(clientActionType){
            case DIR:
                getFiles();
                break;
            case PUSH:
                uploadFile();
                break;
            case PULL:
                downloadFile();
                break;
            case MD5:
                checkMD5();
                break;
        }
    }

    @Override
    public void run() {
        parseGUICommand();
    }
}
