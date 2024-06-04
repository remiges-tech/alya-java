package com.remiges.alya.jobs;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component

@Data
@Scope("prototype")
public class JobMgrClient {


    @Lookup
    public JobMgr getJobMgr() {
        return null;
    }


}
