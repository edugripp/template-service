package com.example.template;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
public class TestWeb {
    public static void main(String[] args) {
        for (java.lang.reflect.Method m : WebMvcConfigurer.class.getDeclaredMethods()) {
            if (m.getName().contains("Converter")) {
                System.out.println(m);
            }
        }
    }
}
