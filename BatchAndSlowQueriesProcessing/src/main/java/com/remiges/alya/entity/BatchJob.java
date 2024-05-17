package com.remiges.alya.entity;

import java.util.UUID;

public interface BatchJob {

    String getapp();

    String getop();

    UUID getbatch();

    Long getrowid(); // unique row ID from batchrows table

    String getcontext();

    Integer getline();

    String getinput();

}
