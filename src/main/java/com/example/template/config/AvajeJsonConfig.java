package com.example.template.config;

import io.avaje.jsonb.Jsonb;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

@Configuration
public class AvajeJsonConfig implements WebMvcConfigurer {

    @Bean
    public Jsonb jsonb() {
        return Jsonb.builder().build();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new AvajeHttpMessageConverter(jsonb()));
    }

    public static class AvajeHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

        private final Jsonb jsonb;

        public AvajeHttpMessageConverter(Jsonb jsonb) {
            super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
            this.jsonb = jsonb;
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return clazz.getName().startsWith("com.example.template");
        }

        @Override
        protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            Type type = clazz;
            try (InputStream is = inputMessage.getBody()) {
                return jsonb.type(type).fromJson(is);
            }
        }

        @Override
        protected void writeInternal(Object object, HttpOutputMessage outputMessage)
                throws IOException, HttpMessageNotWritableException {
            Type type = object.getClass();
            try (OutputStream os = outputMessage.getBody()) {
                jsonb.type(type).toJson(object, os);
            }
        }
    }
}
