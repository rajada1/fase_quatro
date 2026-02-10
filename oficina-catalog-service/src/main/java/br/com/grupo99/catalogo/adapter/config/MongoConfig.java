package br.com.grupo99.catalogo.adapter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuração MongoDB para o Catalog Service.
 * 
 * - Habilita auditing para @CreatedDate e @LastModifiedDate
 * - Configura scan de repositórios MongoDB
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "br.com.grupo99.catalogo.adapter.repository")
public class MongoConfig {
}
