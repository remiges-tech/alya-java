package com.remiges.alya.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class JobManagerConfig {

    @Value("${Alya.batchchunk.rows}")
    private Integer ALYA_BATCHCHUNK_NROWS;

    @Value("${Alya.batchchunk.sec}")
    private Integer ALYA_BATCHSTATUS_CACHEDUR_SEC;

}
