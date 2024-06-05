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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.alya.jobs.SlowQueriesResultList;
import com.remiges.alya.jobs.SlowQuery;
import com.remiges.alya.jobs.SlowQueryResult;
import com.remiges.demoalya.component.SQInitializer;
import com.remiges.demoalya.component.SlowQueryInitBlock;
import com.remiges.demoalya.component.SlowQueryProcessor;

import io.hypersistence.utils.hibernate.type.json.internal.JacksonUtil;

@RestController
@RequestMapping("/api")
public class SlowQueryController {

	@Autowired
	private SlowQuery slowQueryService;

	@Autowired
	private JobMgrClient jobMgrcli;

	@PostMapping("/submit")
	public String submit() {

		String apps = "KRA";
		String ops = "PANENQURY";

		SQInitializer sqInitializer = new SQInitializer();

		JsonNode context = JacksonUtil.toJsonNode("{" +
				"   \"fileName\": \"Transaction.csv\"}");

		String inputQuery = "SELECT * FROM batches"; // Replace with your SQL query

		String batchId = slowQueryService.submit(apps, ops, context, inputQuery);
		JobMgr jobMgr = jobMgrcli.getJobMgr();

		jobMgr.registerInitializer(apps, sqInitializer);

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
		try {
			slowQueryService.abort(reqID);
		} catch (Exception ex) {

		}

	}

	@GetMapping("/list")
	public List<SlowQueriesResultList> list(@RequestParam String app, @RequestParam(required = false) String op,
			@RequestParam int age) {
		return slowQueryService.list(app, op, age);
	}
}
