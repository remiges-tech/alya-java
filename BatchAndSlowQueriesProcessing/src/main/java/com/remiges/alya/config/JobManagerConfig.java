package com.remiges.alya.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.remiges.rigel.service.RigelService;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Component
@Data
public class JobManagerConfig {

    @Autowired
    private RigelService rigelService;

    private Integer ALYA_BATCHCHUNK_NROWS;

    private Integer ALYA_BATCHSTATUS_CACHEDUR_SEC;

    @PostConstruct
    public void getValue() {

        ALYA_BATCHCHUNK_NROWS = Integer.parseInt(
                rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "batchchunkRows"));
        ALYA_BATCHSTATUS_CACHEDUR_SEC = Integer.parseInt(
                rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "batchchunkSec"));
    }

}
