package com.remiges.demoalya.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.alya.jobs.SlowQueriesResultList;
import com.remiges.alya.jobs.SlowQuery;
import com.remiges.alya.jobs.SlowQueryResult;
import com.remiges.demoalya.component.SlowQueryInitBlock;
import com.remiges.demoalya.component.SlowQueryProcessor;

@RestController
@RequestMapping("/api")
public class SlowQueryController {

	@Autowired
	private SlowQuery slowQueryService;

	@Autowired
	private JobMgrClient jobMgrcli;

	@PostMapping("/submit")
	public String submit(@RequestParam String app, @RequestParam String op, @RequestBody JsonNode context,
			@RequestParam String input) {

		String apps = "KRA";
		String ops = "PANENQURY";

		SlowQueryInitBlock initBlock = new SlowQueryInitBlock();
		JsonNode contexts = null; // You can initialize this if needed
		String inputQuery = "SELECT * FROM batches"; // Replace with your SQL query

		String batchId = slowQueryService.submit(apps, ops, contexts, inputQuery);
		JobMgr jobMgr = jobMgrcli.getJobmrg();

		jobMgr.registerInitializer(apps, initBlock);

		jobMgr.RegisterSQProcessor(apps, ops, new SlowQueryProcessor());
		jobMgr.DoJobs();

		return batchId;
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
	public List<SlowQueriesResultList> list(@RequestParam String app, @RequestParam(required = false) String op,
			@RequestParam int age) {
		return slowQueryService.list(app, op, age);
	}
}
