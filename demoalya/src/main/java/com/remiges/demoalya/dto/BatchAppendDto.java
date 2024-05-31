package com.remiges.demoalya.dto;

import lombok.Data;

@Data
public class BatchAppendDto {

    String batchId;

    String base64File;

    Boolean waitoff;

}
