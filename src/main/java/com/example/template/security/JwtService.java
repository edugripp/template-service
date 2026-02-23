package com.example.template.security;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JwtService {

    // Em produção, isso ficaria no cofre de segredos / AWS Secrets / Variável de
    // Ambiente
    private static final String SECRET_KEY = "YourSuperSecretKeyForJwtTokenGenerationMustBeLongEnoughForHs256AtLeast32BytesLength";

    public String generateToken(String username) {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        // Token expira em 1 dia
        String payload = "{\"sub\":\"" + username + "\",\"exp\":" + ((System.currentTimeMillis() / 1000) + 86400) + "}";

        String b64Header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String b64Payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        String signature = hmacSha256(b64Header + "." + b64Payload, SECRET_KEY);
        return b64Header + "." + b64Payload + "." + signature;
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                return false;
            String signature = hmacSha256(parts[0] + "." + parts[1], SECRET_KEY);
            return signature.equals(parts[2]);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int start = payload.indexOf("\"sub\":\"") + 7;
            int end = payload.indexOf("\"", start);
            return payload.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }
}
