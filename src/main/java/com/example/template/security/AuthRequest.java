package com.example.template.security;

import io.avaje.jsonb.Json;

@Json
public record AuthRequest(String username, String password) {
}
