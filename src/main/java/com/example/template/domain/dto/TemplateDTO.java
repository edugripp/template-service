package com.example.template.domain.dto;

import io.avaje.jsonb.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Json
public class TemplateDTO {

    @Json.Property("id")
    private Long id;

    @Json.Property("description")
    private String description;
}