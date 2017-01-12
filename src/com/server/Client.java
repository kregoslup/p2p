package com.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by krego on 28.12.2016.
 */
public class Client implements Runnable{
    private RequestType clientActionType;
    private int portNumber;
    private Socket socket;
    private FileHandler fileHandler;
    private String fileName;
    private ObjectMapper mapper;
    public HashMap<String, byte[]> filesMap;
    private static final int FILE_FIRST_PART = 0;
    private BufferedOutputStream outputStreamWriter;
    private File downloadPath;

    public Client(File downloadPath, RequestType clientActionType, int portNumber, String fileName){
        this.clientActionType = clientActionType;
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.downloadPath = downloadPath;
        configureMapper();
        setUpSocket();
        addHandlers(downloadPath);
    }

    public Client(RequestType clientActionType, int portNumber, File downloadPath){
        this.clientActionType = clientActionType;
        this.portNumber = portNumber;
        this.downloadPath = downloadPath;
        configureMapper();
        setUpSocket();
    }

    private void logRequest(Request request){
        try {
            RequestsLogHandler
                    .getInstance()
                    .logRequest(this.downloadPath, request.getRequestType().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureMapper(){
        this.mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }

    private void addHandlers(File downloadPath){
        this.fileHandler = new FileHandler(downloadPath);
    }

    private void setUpSocket(){
        try {
            socket = new Socket(InetAddress.getByName("localhost"), portNumber);
        } catch (IOException e) {
            throw new Error("Error setting up socket in client");
        }
    }

    private void downloadFile() throws IOException {
        Request request = new Request(RequestType.PULL, Client.FILE_FIRST_PART, fileName);
        String fullFileName = fileHandler.getFullFileName(fileName);
        sendRequest(request);
        Request response = parseIncomingRequest();
        disconnect();
        fileHandler.writeFilePart(fullFileName, response.getDataBase64Array(), 0);
        long fileParts = response.getMaxDataSequence();
        for (int i = 1; i<fileParts; i++){
            reconnect();
            sendRequest(new Request(RequestType.PULL, i, fileName));
            Request loopResponse = parseIncomingRequest();
            fileHandler.writeFilePart(fullFileName, loopResponse.getDataBase64Array(), i);
        }
        disconnect();
    }

    private void disconnect() throws IOException{
        outputStreamWriter.close();
        socket.close();
    }

    private void reconnect(){
        try {
            outputStreamWriter.close();
            setUpSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFile() throws IOException{
        String shortFileName = new File(fileName).getName();
        long fileParts = fileHandler.calculateFileParts(new File(fileName));
        for (long i = 0; i < fileParts; i++){
            if (socket.isClosed()){
                reconnect();
            }
            byte[] dataArray = fileHandler.loadFilePart(fileName, i);
            sendRequest(new Request(
                    RequestType.PUSH, null, i, fileParts, shortFileName, dataArray
            ));
            Request request = parseIncomingRequest();
            if (request.getRequestType() != RequestType.ACK){
                break;
            }
            socket.close();
        }
        outputStreamWriter.close();
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
            outputStreamWriter = new BufferedOutputStream(socket.getOutputStream());
            outputStreamWriter.write(jsonRequest.getBytes(), 0, jsonRequest.getBytes().length);
            outputStreamWriter.flush();
            logRequest(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Got an error while sending message from client");
        }
    }

    private Request parseIncomingRequest(){
        try {
            Request incomingRequest = this.mapper.readValue(socket.getInputStream(), Request.class);
            logRequest(incomingRequest);
            return incomingRequest;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestParseException("Error parsing host request");
        }
    }

    private void getFiles() throws IOException{
        Request request = new Request(RequestType.DIR);
        sendRequest(request);
        Request response = parseIncomingRequest();
        filesMap = response.getFilesMap();
        disconnect();
    }

    private void checkMD5() throws IOException{
        Request request = new Request(RequestType.MD5, fileName);
        try {
            request.setDataMD5(FileHandler.countMD5(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendRequest(request);
        Request response = parseIncomingRequest();
        disconnect();
    }

    private void parseGUICommand() throws IOException{
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
        try {
            parseGUICommand();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Error parsing gui command");
        }
    }
}
