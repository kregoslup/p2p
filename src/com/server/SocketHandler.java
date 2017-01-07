package com.server;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by krego on 05.01.2017.
 */
class SocketHandler {
    static String readSocketStreamToString(BufferedInputStream bufferedInputStream) throws IOException {
        final int bufferSize = 102400;
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        StringBuilder response = new StringBuilder();
        while((bytesRead = bufferedInputStream.read(buffer)) != -1){
            response.append(new String(buffer, 0, bytesRead));
        }
        return response.toString();
    }
}
