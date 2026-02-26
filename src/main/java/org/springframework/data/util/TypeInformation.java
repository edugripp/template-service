package org.springframework.data.util;

/**
 * Ployfill para contornar um bug de versão transiente do springdoc-openapi 2.x
 * que tenta referenciar a classe TypeInformation no pacote antigo do Spring Data 3.x
 * durante a fase de análise estática do compilador AOT.
 * Como o Spring Boot 4 migrou essa classe para org.springframework.data.core, 
 * o AOT nativo quebra com NoClassDefFoundError sem a presença desse stub!
 */
public interface TypeInformation<T> {
}
