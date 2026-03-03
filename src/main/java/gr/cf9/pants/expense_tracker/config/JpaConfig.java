package gr.cf9.pants.expense_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing      // timestamp
@Configuration
public class JpaConfig {
}
