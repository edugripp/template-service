package com.example.template.persistence.dao;


import com.example.template.domain.dto.TemplateDTO;
import com.example.template.persistence.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITemplateDAO extends JpaRepository<Template, Long> {

    @Query(value = "SELECT new com.example.template.domain.dto.TemplateDTO (t.id, t.description) from template t", nativeQuery = false)
    Page<TemplateDTO> findAllTemplates(Pageable pageable);

}
