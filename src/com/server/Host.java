package com.server;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

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
    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 49151;

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
        String defaultPath = new StringBuilder("C:\\TORRENT_").append(hostNumber).toString();
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

    public void abort(){
        hostStatus = false;
        executorService.shutdown();
    }

    private void socketListeningLoop() throws IOException {
        while(hostStatus) {
            executorService.execute(new RequestHandler(serverSocket.accept(), this.filesMap, downloadPath));
        }
    }

    @Override
    public void run() {
        try {
            socketListeningLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RequestHandler implements Runnable{
        private final Socket socket;
        private RequestParser parser;

        RequestHandler(Socket socket, HashMap<String, byte[]> filesMap, File downloadPath){
            this.socket = socket;
            parser = new RequestParser(filesMap, downloadPath);
        }

        Request parseRequest() {
            try {
                return parser.parseIncomingRequest(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestParseException("Error parsing incoming message");
            }
        }

        void sendResponse(Request request) throws IOException {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            String outRequest = parser.parseOutgoingRequest(request, filesMap);
            System.out.println(outRequest);
            out.write(outRequest.getBytes());
            out.flush();
        }

        void handleAbortedConnection(Exception e){
            e.printStackTrace();
        }

        @Override
        public void run() {
            try {
                Request request = parseRequest();
                sendResponse(request);
            } catch (IOException | RequestParseException e) {
                handleAbortedConnection(e);
            }
        }
    }
}
