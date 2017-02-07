package com.request;

import java.util.HashMap;

/**
 * Created by krego on 07.02.2017.
 */
public interface ResponseParser {
    String parseOutgoingRequest(
            Request request,
            HashMap<String, byte[]> filesMap
    ) throws RequestParseException;
}
