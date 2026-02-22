package com.example.template.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.template.service.interfaces.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import java.util.function.Function;

@Configuration
public class LambdaHandlerConfig {
    
    @Autowired
    private ITemplateService templateService;

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> function() {
        return request -> {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            
            try {
                if ("GET".equals(request.getHttpMethod()) && "/template".equals(request.getPath())) {
                    ResponseEntity<?> serviceResponse = templateService.getTemplate(1L); // Example ID
                    response.setStatusCode(serviceResponse.getStatusCode().value());
                    response.setBody(serviceResponse.getBody().toString());
                } else {
                    response.setStatusCode(404);
                    response.setBody("Not Found");
                }
            } catch (Exception e) {
                response.setStatusCode(500);
                response.setBody("Internal Server Error: " + e.getMessage());
            }
            
            return response;
        };
    }
} 