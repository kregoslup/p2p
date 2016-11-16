package com.company;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by krego on 06.11.2016.
 */
public class Response<C> {
    private C content;

    Response() {

    }

    private void getFilesList(ArrayList<String> filesList){

    }

    private void sendFilePart(){

    }

    private void countMD5(){

    }

//    static Response prepareResponse(Request request, ArrayList<String> filesMap, int currentPath, HostStatus status) {
//        Response response = new Response();
//        switch(request.getRequestType()){
//            case LIST:
//                response.getFilesList(filesMap);
//            case DOWNLOAD:
//                response.sendFilePart(request);
//            case MD5:
//                response.countMD5(request);
//            case ACCEPT:
//                if(status == HostStatus.SENDING){
//                    response.sendFilePart();
//                }else{
//                    break;
//                }
//        }
//    }
}
