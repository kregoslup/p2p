package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.10.2016.
 */
class Host implements Runnable{
    public final static String HASHING_ALGO = "MD5";

    private final ServerSocket serverSocket;
    private File downloadPath;
    private final ExecutorService executorService;
    private ArrayList<String> filesList;

    public Host(int portNumber, int hostNumber, int threadPoolSize) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        String defaultPath = new StringBuilder("C:\\TORRENT_").append(hostNumber).toString();
        downloadPath = new File(defaultPath);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        discoverFiles(downloadPath);
    }

    private void discoverFiles(File path){
        if (path.listFiles() != null && path.isDirectory() && path.exists()){
            for(File file: path.listFiles()){
                if(file.isFile()){
                    if(generateMD5(file).isPresent()){
                        filesList.add(file.getName());
                    }
                }else{
                    discoverFiles(file);
                }
            }
        }
    }

    private Optional<byte[]> generateMD5(File file){
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(Host.HASHING_ALGO);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileBytes = new byte[4096];
            while(fileInputStream.available() > 0){
                fileInputStream.read(fileBytes);
                messageDigest.update(fileBytes);
            }
            return Optional.of(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void socketListeningLoop() throws IOException {
        while(true) {
            executorService.submit(new RequestHandler(serverSocket.accept()));
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


        RequestHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Request request = new RequestParser(inStream).parseRequest();
                Response response = Response.prepareResponse(request, Host.this.filesList, currentPart, status);
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(response.getEncodedMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
