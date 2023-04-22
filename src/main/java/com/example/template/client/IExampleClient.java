package com.example.template.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "Example", url = "https://www.google.com.br/", dismiss404 =  true)
public interface IExampleClient {

	@GetMapping(path = "/", consumes = MediaType.TEXT_HTML_VALUE)
	ResponseEntity<String> getGoogleHomePage();
}