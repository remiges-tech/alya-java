package com.remiges.demoalya.dto;

import lombok.Data;

@Data
public class BatchInputDto {

    String batchId;

    String base64File;

    Boolean waitoff;

}
