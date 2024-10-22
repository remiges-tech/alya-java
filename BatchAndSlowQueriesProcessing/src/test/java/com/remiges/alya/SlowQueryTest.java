package com.remiges.alya;

public class SlowQueryTest {

    // private SlowQuery slowQuery;
    // private BatchJobService batchJobServiceMock;
    // private JedisService jedisServiceMock;

    // @BeforeEach
    // public void setUp() {
    //     batchJobServiceMock = mock(BatchJobService.class);
    //     jedisServiceMock = mock(JedisService.class);
    //     slowQuery = new SlowQuery(batchJobServiceMock, jedisServiceMock, null);
    // }

    // @Test
    // public void testSubmit() throws JsonMappingException, JsonProcessingException {
    //     // Define inputs
    //     String app = "TestApp";
    //     String op = "TestOp";
    //     // Define your JSON strings
    //     String contextJsonString = "{\"key\": \"value\"}";
    //     String inputJsonString = "{\"key\": \"value\"}";

    //     // Create ObjectMapper instance
    //     ObjectMapper objectMapper = new ObjectMapper();

    //     // Convert JSON strings to JsonNode objects
    //     JsonNode context = objectMapper.readTree(contextJsonString);
    //     JsonNode input = objectMapper.readTree(inputJsonString);
    //     // Stub the service method to return a UUID
    //     UUID requestId = UUID.randomUUID();

    //     when(batchJobServiceMock.saveSlowQueries(app, op, context, inputJsonString, BatchStatus.BatchQueued))
    //             .thenReturn(requestId);

    //     // Call the method
    //     String result = slowQuery.submit(app, op, context, inputJsonString);
    //     System.out.println(result);

    //     // Verify the result
    //     assertNotNull(result);
    //     assertEquals(requestId.toString(), result);
    // }

    // @Test
    // public void testDone() throws Exception {
    //     // Define inputs
    //     UUID requestId = UUID.randomUUID();
    //     // Stub the service methods
    //     when(jedisServiceMock.getBatchStatusFromRedis("ALYA_BATCHSTATUS_" + requestId)).thenReturn("BatchTryLater");
    //     when(batchJobServiceMock.getSlowQueryStatusByReqId(requestId.toString())).thenReturn(BatchStatus.BatchSuccess);
    //     Batches batch = new Batches();
    //     batch.setId(requestId);
    //     batch.setStatus(BatchStatus.BatchSuccess);
    //     BatchRows batchRow = new BatchRows();
    //     batchRow.setBatchStatus(BatchStatus.BatchSuccess);
    //     when(batchJobServiceMock.getSQBatchRowByReqId(requestId.toString())).thenReturn(batchRow);
    //     doNothing().when(batchJobServiceMock).updateBatchRowStatus(batchRow.getRowId(), BatchStatus.BatchSuccess);

    //     // Call the method
    //     SlowQueryResult result = slowQuery.done(requestId.toString());

    //     // Verify the result
    //     assertNotNull(result);
    //     assertEquals(BatchStatus.BatchSuccess, result.getStatus());
    // }

    // @Test
    // public void testAbort() throws Exception {
    //     // Define inputs
    //     UUID requestId = UUID.randomUUID();
    //     // Stub the service methods
    //     Batches batch = new Batches();
    //     batch.setId(requestId);
    //     batch.setStatus(BatchStatus.BatchInProgress);
    //     when(batchJobServiceMock.getBatchByReqId(requestId.toString())).thenReturn(batch);

    //     // Call the method
    //     slowQuery.abort(requestId.toString());

    //     // Verify that the correct method was called with the correct parameters
    //     verify(batchJobServiceMock).abortBatchAndRows(batch);
    // }

    // @Test
    // public void testList() {
    //     // Define inputs
    //     String app = "TestApp";
    //     String op = "TestOp";
    //     int age = 7; // age in days
    //     LocalDateTime thresholdTime = LocalDateTime.now().minusDays(age);
    //     List<Batches> batchRowsList = new ArrayList<>();
    //     // Add some dummy batch rows to the list
    //     Batches batch1 = new Batches();
    //     batch1.setId(UUID.randomUUID());
    //     batch1.setApp(app);
    //     batch1.setOp(op);
    //     // batch1.setReqat(LocalDateTime.now().minusDays(5)); // within the age limit
    //     batchRowsList.add(batch1);
    //     // Add another dummy batch row outside the age limit
    //     Batches batch2 = new Batches();
    //     batch2.setId(UUID.randomUUID());
    //     batch2.setApp(app);
    //     batch2.setOp(op);
    //     // batch2.setReqat(LocalDateTime.now().minusDays(10)); // outside the age limit
    //     batchRowsList.add(batch2);
    //     // Stub the service method
    //     when(batchJobServiceMock.findBatchesByAppAndOpAndReqAtAfter(app, op, thresholdTime))
    //             .thenReturn(batchRowsList);

    //     // Call the method
    //     List<SlowQueriesResultList> result = slowQuery.list(app, op, age);

    //     // Verify the result
    //     assertNotNull(result);
    //     assertEquals(1, result.size()); // Only one batch should be within the age limit
    //     assertEquals(batch1.getId().toString(), result.get(0).getId());
    // }
}
