package com.remiges.alya.json;

import com.remiges.alya.dto.RequestDTO;
import com.remiges.alya.jsonutil.util.model.AlyaSuccessResponse;
import com.remiges.alya.jsonutil.util.model.JsonRequest;

public class AlyaFormator {

     // Method to format the request according to Alya WSC format
     public JsonRequest formatRequest(RequestDTO request) {
        JsonRequest alyaRequest = new JsonRequest();
        // Example of formatting logic
        alyaRequest.setFormattedData("Formatted: " + request.getData());
        alyaRequest.setFormattedMetadata("Meta: " + request.getMetadata());
        return alyaRequest;
    }

    // Method to format the response according to Alya WSC format
    public AlyaSuccessResponse formatResponse(ResponseDTO response) {
        AlyaSuccessResponse alyaResponse = new AlyaSuccessResponse();
        
        if (response.isSuccess()) {
            alyaResponse.setStatus("Success");
            alyaResponse.setMessage("Request processed successfully");
            alyaResponse.setResponseData(response.getData());
        } else {
            alyaResponse.setStatus("Error");
            alyaResponse.setMessage(response.getMessage());
            alyaResponse.setResponseData(null);
        }
        
        return alyaResponse;
    }

}
