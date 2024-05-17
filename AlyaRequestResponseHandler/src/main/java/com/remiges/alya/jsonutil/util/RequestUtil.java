package com.remiges.alya.jsonutil.util;

import com.remiges.alya.jsonutil.util.model.JsonRequest;

public final class RequestUtil {

    private RequestUtil() {
        // private constructor to prevent instantiation
    }

    // Static method to create JsonRequest
    public static JsonRequest create(Object data) {
        return new JsonRequest(data);
    }
}

// JsonRequest request = RequestUtil.create(yourDataObject);
// We have to pass this to create data as an Object


