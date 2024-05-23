package com.remiges.alya.jsonutil.util.model;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaSuccessResponse {

    private String status;
    private DataObject data;
    private List<String> messages;

}

