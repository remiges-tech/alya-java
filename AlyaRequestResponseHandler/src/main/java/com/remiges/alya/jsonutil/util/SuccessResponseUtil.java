package com.remiges.alya.jsonutil.util;

import java.util.Collections;
import java.util.List;

import com.remiges.alya.jsonutil.util.model.SuccessResponse;

public final class SuccessResponseUtil {

    private SuccessResponseUtil() {
        // private constructor to prevent instantiation
    }

    // Static method to create SuccessResponse
    public static <T> SuccessResponse<T> success(T data) {
        return new SuccessResponse<>("success", data, Collections.emptyList());
    }
}

