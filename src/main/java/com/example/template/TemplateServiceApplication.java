package com.example.template;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.modelmapper.ModelMapper;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

@Primary
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.template")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@ImportRuntimeHints(TemplateServiceApplication.MyRuntimeHints.class)
@SecurityScheme(name = "Authorization", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class TemplateServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(TemplateServiceApplication.class, args);

	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	static class MyRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			// Register serialization
			hints.serialization().registerType(HashMap.class).registerType(ArrayList.class);

		}

	}

//	@Bean
//	public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
//		ResourceRetriever resourceRetriever
//				= new DefaultResourceRetriever(jwtConfiguration.getConnectionTimeout(),
//				jwtConfiguration.getReadTimeout());
//		URL jwkSetURL = new URL(jwtConfiguration.getJwkUrl());
//		JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
//		ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
//		JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
//		jwtProcessor.setJWSKeySelector(keySelector);
//		return jwtProcessor;
//	}

//	@Bean
//	public OpenAPI customOpenAPI() {
//		return new OpenAPI()
//				//bearer auth with oAuth2
//				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//				.components(new Components().addSecuritySchemes("oAuth2",                   new SecurityScheme()                                                                .type(SecurityScheme.Type.OAUTH2)                                                               .flows(getOAuthFlows())
//								)
//
///// Bearer AUTH security config settings. ## for id-token.
//								.addSecuritySchemes("bearerAuth",                                                       new SecurityScheme()
//										.type(SecuritySchemeType.HTTP)
//										.scheme("bearer")
//										.bearerFormat("JWT")
//								)
//				).info(getInfo());
//	}
//
//
//	private Info getInfo() {
//		return new Info()
//				.title("Title")
//				.version("1.0")
//				.description("Project description..");
//	}
}
