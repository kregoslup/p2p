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
    private static int hostsCount;
    private final ServerSocket serverSocket;
    private File downloadPath;
    public IntegerProperty portNumber;
    private final ExecutorService executorService;
    private HashMap<String, byte[]> filesMap;
    private IntegerProperty hostNumber;
    private BooleanProperty hostStatus = new SimpleBooleanProperty(true);
    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 49151;

    public Host(int portNumber, int threadPoolSize) throws IOException {
        hostsCount += 1;
        this.portNumber = new SimpleIntegerProperty(portNumber);
        this.serverSocket = new ServerSocket(portNumber);
        this.hostNumber = new SimpleIntegerProperty(hostsCount);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.filesMap = new HashMap<>();
        configureDownloadPath();
    }

    private void configureDownloadPath(){
        setDownloadPath();
        createFolderIfNotExisting();
        discoverFiles(downloadPath);
    }

    public boolean getHostStatus(){
        return hostStatus.get();
    }

    public int getHostNumber(){
        return hostNumber.get();
    }

    public int getPortNumber(){
        return portNumber.get();
    }

    private void setDownloadPath(){
        String defaultPath = new StringBuilder("C:\\TORRENT_").append(hostNumber.get()).toString();
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

    public void abort(){
        hostStatus.setValue(false);
        executorService.shutdown();
    }

    private void socketListeningLoop() throws IOException {
        while(hostStatus.get()) {
            executorService.execute(new RequestHandler(serverSocket.accept(), this.filesMap));
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

        RequestHandler(Socket socket, HashMap<String, byte[]> filesMap){
            this.socket = socket;
            parser = new RequestParser(filesMap);
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
