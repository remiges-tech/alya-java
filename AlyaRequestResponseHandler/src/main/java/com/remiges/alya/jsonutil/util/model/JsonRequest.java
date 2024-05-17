package com.remiges.alya.jsonutil.util.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonRequest {

    private Object data;

    // Using lombok for constructor and getter + setter
}
