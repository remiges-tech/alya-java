package com.remiges.alya.jobs;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component

@Data
public class JobMgrClient {

    private JobMgr jobmrg;

    JobMgrClient(JobMgr jobmrg) {
        this.jobmrg = jobmrg;
    }

}
