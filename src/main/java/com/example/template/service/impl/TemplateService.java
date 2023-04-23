package com.example.template.service.impl;

import com.example.template.domain.dto.TemplateDTO;
import com.example.template.persistence.dao.ITemplateDAO;
import com.example.template.persistence.entity.Template;
import com.example.template.service.interfaces.ITemplateService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TemplateService implements ITemplateService {

	@Autowired
	ITemplateDAO templateRepository;

	@Autowired
	ModelMapper modelMapper;

	protected TemplateDTO convertToDto(Template template){
		return modelMapper.map(template, TemplateDTO.class);
	}

	protected Template convertToEntity(TemplateDTO templateDTO){
		return modelMapper.map(templateDTO, Template.class);
	}

	@Override
	public ResponseEntity<TemplateDTO> createTemplate(TemplateDTO templateDTO){
		Template template = convertToEntity(templateDTO);
		Optional<Template> optionalTemplate = templateRepository.findOne(Example.of(template));
		if(optionalTemplate.isPresent()){
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return new ResponseEntity<>(
				convertToDto(templateRepository.save(template)),
				HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<TemplateDTO> updateTemplate(Long id, TemplateDTO templateDTO){
		Template template = this.convertToEntity(templateDTO);
		Optional<Template> optionalTemplate = templateRepository.findOne(Example.of(template));
		if(optionalTemplate.isPresent()){

			return new ResponseEntity<>(convertToDto(templateRepository.save(template)),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<TemplateDTO> getTemplate(Long id){

		Optional<Template> optionalTemplate = templateRepository.findById(id);

		return optionalTemplate.map(template -> new ResponseEntity<>(this.convertToDto(template), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	@Override
	public ResponseEntity<Page<TemplateDTO>> getTemplates(int page, int size) {

		return new ResponseEntity<>(templateRepository.findAllTemplates(PageRequest.of(page - 1, size)), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Boolean> deleteTemplate(Long id){
		templateRepository.deleteById(id);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}


}