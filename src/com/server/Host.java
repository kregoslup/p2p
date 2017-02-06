package com.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.request.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.10.2016.
 */

public class Host implements Runnable{
    private final ServerSocket serverSocket;
    private File downloadPath;
    public int portNumber;
    private final ExecutorService executorService;
    private HashMap<String, byte[]> filesMap;
    private int hostNumber;
    private boolean hostStatus = true;

    public Host(int portNumber, int threadPoolSize, int appNumber) throws IOException {
        this.portNumber = portNumber;
        this.serverSocket = new ServerSocket(portNumber);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.filesMap = new HashMap<>();
        this.hostNumber = appNumber;
        configureDownloadPath();
    }

    private void configureDownloadPath(){
        setDownloadPath();
        createFolderIfNotExisting();
        discoverFiles(downloadPath);
    }

    private void setDownloadPath(){
        String defaultPath = new StringBuilder("D:\\TORrent_").append(hostNumber).toString();
        downloadPath = new File(defaultPath);
    }

    private void createFolderIfNotExisting(){
        if (!downloadPath.exists() || downloadPath.isFile()){
            boolean created = downloadPath.mkdir();
            if (!created){
                throw new Error("Error creating home directory for new host");
            }
        }
    }

    private void discoverFiles(File path){
        if (path.listFiles() != null && path.isDirectory() && path.exists()){
            for(File file: path.listFiles()){
                if(file.isFile()){
                    try {
                        byte[] md5Hash = FileHandler.countMD5(file.getAbsolutePath());
                        filesMap.put(file.getName(), md5Hash);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    discoverFiles(file);
                }
            }
        }
    }

    public File getDownloadPath(){
        return downloadPath;
    }

    void abort(){
        hostStatus = false;
        executorService.shutdown();
    }

    private void socketListeningLoop() throws IOException {
        while(hostStatus) {
            executorService.execute(new RequestHandler(serverSocket.accept(), this.filesMap, downloadPath));
            discoverFiles(downloadPath);
        }
    }

    @Override
    public void run() {
        try {
            socketListeningLoop();
        } catch (IOException e) {
            e.printStackTrace();
            abort();
        }
    }

    private class RequestHandler implements Runnable{
        private final Socket socket;
        private RequestParser parser;
        private File downloadPath;
        private HashMap<String, byte[]> filesMap;

        RequestHandler(Socket socket, HashMap<String, byte[]> filesMap, File downloadPath){
            this.socket = socket;
            this.downloadPath = downloadPath;
            this.filesMap = filesMap;
            parser = new RequestParser(downloadPath);
        }

        Request parseRequest() {
            try {
                return parser.parseIncomingRequest(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestParseException("Error parsing incoming message");
            }
        }

        private void logRequest(String request) throws IOException{
            try {
                Request requestToLog = parser.getMapper().readValue(request, Request.class);
                RequestsLogHandler.getInstance().logRequest(this.downloadPath, "Host: " + requestToLog.getRequestType().getRequestTypeVerbose());
            } catch (JsonParseException e) {
                RequestsLogHandler.getInstance().logRequest(this.downloadPath, request);
            }
        }

        private void logRequest(Request request){
            try {
                RequestsLogHandler
                        .getInstance()
                        .logRequest(this.downloadPath, "Host: " + request.getRequestType().getRequestTypeVerbose());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendResponse(Request request) throws IOException {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            String outRequest;
            if(!(request.getRequestType() == RequestType.HTML)) {
                outRequest = parser.parseOutgoingRequest(request, filesMap);
                out.write(outRequest.getBytes(), 0, outRequest.getBytes().length);
            }else{
                String response = HTMLParser.createResponse(socket.getInputStream(), downloadPath);
                out.write(response.getBytes());
                outRequest = "HTML: Log file";
            }
            out.flush();
            out.close();
            logRequest(outRequest);
        }

        void handleAbortedConnection(Exception e){
            e.printStackTrace();
        }

        @Override
        public void run() {
            try {
                Request request = parseRequest();
                sendResponse(request);
                socket.close();
                logRequest(request);
            } catch (IOException | RequestParseException e) {
                handleAbortedConnection(e);
            }
        }
    }
}
