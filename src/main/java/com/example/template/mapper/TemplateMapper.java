package com.example.template.mapper;

import com.example.template.domain.dto.TemplateDTO;
import com.example.template.persistence.entity.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    TemplateDTO toDto(Template template);

    @Mapping(target = "active", ignore = true)
    Template toEntity(TemplateDTO templateDTO);
}
