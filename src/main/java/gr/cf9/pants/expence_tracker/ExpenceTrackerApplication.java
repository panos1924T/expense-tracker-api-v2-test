package gr.cf9.pants.expence_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class ExpenceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenceTrackerApplication.class, args);
	}

}
