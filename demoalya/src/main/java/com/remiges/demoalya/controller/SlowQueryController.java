package com.remiges.demoalya.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.SlowQueriesResultList;
import com.remiges.alya.jobs.SlowQuery;
import com.remiges.alya.jobs.SlowQueryResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SlowQueryController {

    @Autowired
    private SlowQuery slowQueryService;

    @PostMapping("/submit")
    public String submit(@RequestParam String app,
                        @RequestParam String op,
                        @RequestBody JsonNode context,
                        @RequestParam String input) {
        return slowQueryService.submit(app, op, context, input);
    }

    @GetMapping("/done")
    public SlowQueryResult done(@RequestParam String reqID) {
        return slowQueryService.done(reqID);
    }

    @PostMapping("/abort")
    public void abort(@RequestParam String reqID) throws Exception {
        slowQueryService.abort(reqID);
    }

    @GetMapping("/list")
    public List<SlowQueriesResultList> list(@RequestParam String app,
                                            @RequestParam(required = false) String op,
                                            @RequestParam int age) {
        return slowQueryService.list(app, op, age);
    }
}
