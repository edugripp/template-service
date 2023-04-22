package com.example.template.service.interfaces;

import com.example.template.domain.dto.TemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ITemplateService {

    ResponseEntity<TemplateDTO> createTemplate(TemplateDTO templateDTO);

    ResponseEntity<TemplateDTO> updateTemplate(Long id, TemplateDTO templateDTO);

    ResponseEntity<TemplateDTO> getTemplate(Long id);

    ResponseEntity<Page<TemplateDTO>> getTemplates(int page, int size);

    ResponseEntity<Boolean> deleteTemplate(Long id);
}
