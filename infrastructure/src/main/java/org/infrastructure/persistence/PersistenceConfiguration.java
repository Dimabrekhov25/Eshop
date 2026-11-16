package org.infrastructure.persistence;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = PersistenceConfiguration.class)
public class PersistenceConfiguration {
}

