package pl.skyroster.skyroster_backend;

import org.springframework.boot.SpringApplication;

public class TestSkyrosterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(SkyrosterBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
