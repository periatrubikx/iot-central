package io.rubikx.rubikxcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@ComponentScan("io.rubikx.rubikxcore")
@EnableJpaRepositories("io.rubikx.rubikxcore.repository")
@EntityScan("io.rubikx.rubikxcore.domain")
@EnableTransactionManagement
public class RubikXConfig {
}
