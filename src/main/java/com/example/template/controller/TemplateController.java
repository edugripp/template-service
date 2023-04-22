package com.example.template.controller;

import com.example.template.client.IExampleClient;
import com.example.template.domain.dto.TemplateDTO;
import com.example.template.service.interfaces.ITemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE} )
@RequestMapping("/template")
@RestController
@Slf4j
@Validated
@SecurityRequirement(name = "Authorization")

public class TemplateController {

	@Autowired
	ITemplateService templateService;

	@Autowired
	IExampleClient exampleClient;

	@GetMapping(value = "/google")
	public ResponseEntity<String> getGoogle(){
		return exampleClient.getGoogleHomePage();
	}

	@PostMapping(value = "/")
	@Operation(description = "Create template")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "201", description = "Template created"),
					@ApiResponse(responseCode = "422", description = "Template already exist", content = @Content( schema = @Schema( not = TemplateDTO.class)))
			}
	)
	public ResponseEntity<TemplateDTO> createTemplate(@RequestBody TemplateDTO templateDTO) {
		return templateService.createTemplate(templateDTO);
	}

	@PutMapping(value = "/{id}")
	@Operation(description = "Update template")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Template updated"),
					@ApiResponse(responseCode = "404", description = "Template not found", content = @Content( schema = @Schema( not = TemplateDTO.class)))
			}
	)
	public ResponseEntity<TemplateDTO> updateTemplate(@PathVariable(value = "id") Long id, @RequestBody TemplateDTO templateDTO) {

		return templateService.updateTemplate(id, templateDTO);
	}

	@GetMapping(value = "/{id}")
	@Operation(description = "Get template")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Template found"),
					@ApiResponse(responseCode = "404", description = "Template not found", content = @Content( schema = @Schema( not = TemplateDTO.class)))
			}
	)
	public ResponseEntity<TemplateDTO> getTemplate(@PathVariable(value = "id") Long id) {

		return templateService.getTemplate(id);
	}

	@GetMapping()
	@Operation(description = "Get paginated templates")
	@Parameter(hidden = true, name = "sort")
	@Parameter(hidden = true, name = "sortField")
	@ApiResponse(responseCode = "200", description = "Template found")
	public ResponseEntity<Page<TemplateDTO>> getTemplates(@RequestParam(defaultValue = "1") @Positive int page,
														  @RequestParam(defaultValue = "10") @Min(10) @Max(100) int pageSize,
														  @RequestParam(defaultValue = "asc", required = false) String sort,
														  @RequestParam(defaultValue = "id", required = false) String sortField
	) {

		return templateService.getTemplates(page, pageSize);
	}

	@DeleteMapping(value = "/{id}")
	@Operation(description = "Soft delete template")
	@ApiResponse(responseCode = "200", description = "Template found")
	public ResponseEntity<Boolean> deleteTemplate(@PathVariable(value = "id") Long id) {

		return templateService.deleteTemplate(id);
	}
}
