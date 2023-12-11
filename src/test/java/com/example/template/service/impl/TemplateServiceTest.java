package com.example.template.service.impl;

import com.example.template.domain.dto.TemplateDTO;
import com.example.template.persistence.dao.ITemplateDAO;
import com.example.template.persistence.entity.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Profile("test")
public class TemplateServiceTest {

    @Mock
    private ITemplateDAO templateRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private TemplateService templateService;

    private TemplateDTO templateDTO;
    private Template template;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create a sample TemplateDTO object
        templateDTO = new TemplateDTO();
        templateDTO.setId(1L);
        templateDTO.setDescription("Template 1");

        // Create a sample Template object
        template = new Template();
        template.setId(1L);
        template.setDescription("Template 1");

        //mocking ModelMapper conversions
        doReturn(templateDTO).when(templateService).convertToDto(any(Template.class));
        doReturn(template).when(templateService).convertToEntity(any(TemplateDTO.class));
    }

    @Test
    @DisplayName("Create Template - Success")
    public void testCreateTemplateSuccess() {
        when(templateRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(templateRepository.save(any(Template.class))).thenReturn(template);


        ResponseEntity<TemplateDTO> response = templateService.createTemplate(templateDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(templateDTO, response.getBody());
    }

    @Test
    @DisplayName("Create Template - Failure")
    public void testCreateTemplateFailure() {
        when(templateRepository.findOne(any(Example.class))).thenReturn(Optional.of(template));

        ResponseEntity<TemplateDTO> response = templateService.createTemplate(templateDTO);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Update Template - Success")
    public void testUpdateTemplateSuccess() {
        when(templateRepository.findById(any())).thenReturn(Optional.of(template));
        when(templateRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(templateRepository.save(any(Template.class))).thenReturn(template);
        when(modelMapper.map(any(Template.class), any())).thenReturn(templateDTO);

        ResponseEntity<TemplateDTO> response = templateService.updateTemplate(1L, templateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(templateDTO, response.getBody());
    }

    @Test
    @DisplayName("Update Template - Failure")
    public void testUpdateTemplateFailure() {
        when(templateRepository.findOne(any(Example.class))).thenReturn(Optional.empty());

        ResponseEntity<TemplateDTO> response = templateService.updateTemplate(1L, templateDTO);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get Template - Success")
    public void testGetTemplateSuccess() {
        when(templateRepository.findById(any())).thenReturn(Optional.of(template));
        when(modelMapper.map(any(Template.class), any())).thenReturn(templateDTO);

        ResponseEntity<TemplateDTO> response = templateService.getTemplate(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(templateDTO, response.getBody());
    }

    @Test
    @DisplayName("Get Template - Failure")
    public void testGetTemplateFailure() {
        when(templateRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<TemplateDTO> response = templateService.getTemplate(1L);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Template - Fill")
    public void testGetTemplatesWhenPageContentIsNotEmpty() {
        // Arrange
        List<TemplateDTO> templates = Collections.singletonList(templateDTO);
        Page<TemplateDTO> page = new PageImpl<>(templates, PageRequest.of(0,10), 1);
        when(templateRepository.findAllTemplates(any(PageRequest.class))).thenReturn(page);

        // Act
        Page<TemplateDTO> result = templateService.getTemplates(1, 10).getBody();

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Get Template - Fill")
    public void testGetTemplatesWhenPageContentIsEmpty() {
        // Arrange
        Page<TemplateDTO> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0,10), 0);
        when(templateRepository.findAllTemplates(any(PageRequest.class))).thenReturn(page);

        // Act
        Page<TemplateDTO> result = templateService.getTemplates(1, 10).getBody();

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getTotalElements());
        Assertions.assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("Delete Template")
    public void testDeleteTemplate() {
        doNothing().when(templateRepository).deleteById(any());

        ResponseEntity<Boolean> response = templateService.deleteTemplate(1L);

        verify(templateRepository, times(1)).deleteById(any());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Boolean.TRUE, response.getBody());
    }

}