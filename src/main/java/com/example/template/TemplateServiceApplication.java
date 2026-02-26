package com.example.template;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Primary
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.template")
@SpringBootApplication
@ImportRuntimeHints(TemplateServiceApplication.MyRuntimeHints.class)
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class TemplateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateServiceApplication.class, args);

	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	static class MyRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			// Register serialization
			hints.serialization().registerType(HashMap.class).registerType(ArrayList.class);
			try {
				hints.reflection().registerType(
						org.hibernate.dialect.MySQLDialect.class,
						org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
						org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
			} catch (Exception e) {
			}
		}

	}

}
