package com.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.10.2016.
 */
class Host implements Runnable{
    private final ServerSocket serverSocket;
    private File downloadPath;
    private final ExecutorService executorService;
    private HashMap<String, byte[]> filesMap;
    private int hostNumber;

    public Host(int portNumber, int hostNumber, int threadPoolSize) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        this.hostNumber = hostNumber;
        String defaultPath = new StringBuilder("C:\\TORRENT_").append(hostNumber).toString();
        downloadPath = new File(defaultPath);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    private void discoverFiles(File path){
        if (path.listFiles() != null && path.isDirectory() && path.exists()){
            for(File file: path.listFiles()){
                if(file.isFile()){
                    try {
                        byte[] md5Hash = this.countMD5(file.getAbsolutePath());
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
    private byte[] countMD5(String file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream inputStream = Files.newInputStream(Paths.get(file));
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, md);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private void socketListeningLoop() throws IOException {
        while(true) {
            discoverFiles(downloadPath);
            executorService.submit(new RequestHandler(serverSocket.accept(), this.filesMap));
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
        private HostStatus status = HostStatus.STARTED;
        private int currentPart = -1;
        private boolean isConnected = true;
        private RequestParser parser;
        private HashMap<String, byte[]> filesMap;

        RequestHandler(Socket socket, HashMap<String, byte[]> filesMap){
            this.socket = socket;
            parser = new RequestParser(filesMap);
        }

        Request parseRequest() {
            try {
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                return parser.parseIncomingRequest(inStream);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestParseException();
            }
        }

        void sendResponse(Request request) throws IOException {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            String outRequest = parser.parseOutgoingRequest(request, currentPart, status, filesMap);
            out.write(outRequest);
        }

        void handleAbortedConnection(Exception e){
            isConnected = false;
            e.printStackTrace();
        }

        @Override
        public void run() {
            try {
                while(isConnected){
                    Request request = parseRequest();
                    sendResponse(request);
                }
            } catch (IOException | RequestParseException e) {
                handleAbortedConnection(e);
            }
        }
    }
}
