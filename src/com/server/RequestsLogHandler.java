package com.server;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by krego on 12.01.2017.
 */
class RequestsLogHandler {
    private static final RequestsLogHandler handler = new RequestsLogHandler();
    private static final String LOG_FILE_NAME = "Log.txt";

    public static RequestsLogHandler getInstance(){
        return handler;
    }

    public String getLogFileName(){
        return LOG_FILE_NAME;
    }

    private String getCurrentTime(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    synchronized void logRequest(File downloadPath, String request) throws IOException {
        File logFile = new File(Paths.get(downloadPath.toString(), LOG_FILE_NAME).toString());
        boolean created = logFile.createNewFile();
        BufferedWriter bufferedWriter;
        try {
            FileWriter fileWriter = new FileWriter(logFile, !created);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(getCurrentTime() + " " + request +'\n');
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
