package com.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * Created by krego on 11.01.2017.
 */
class HTMLParser {
    private static final String HTML_RESPONSE_HEADER = "HTTP/1.1 200 OK\r\nServer: SKJ\r\nContent-Type: text/html; charset=utf-8\r\nConnection: close";
    static String createResponse(InputStream inputStream, File downloadPath) throws IOException {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String request;
//        while ((request = bufferedReader.readLine()) != null){
//            request = bufferedReader.readLine();
//        }
        return logToHtmlRequest(downloadPath);
    }

    private static String logToHtmlRequest(File downloadPath) throws IOException {
        File file = new File(Paths.get(downloadPath.toString(), RequestsLogHandler
                .getInstance()
                .getLogFileName())
                .toString());
        String logContent = new String(Files.readAllBytes(file.toPath()));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(HTML_RESPONSE_HEADER);
        stringBuffer.append(logContent);
        stringBuffer.append("\r\n");
        System.out.print(stringBuffer.toString());
        return stringBuffer.toString();
    }
}
