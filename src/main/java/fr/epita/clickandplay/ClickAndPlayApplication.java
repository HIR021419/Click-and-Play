package fr.epita.clickandplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClickAndPlayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClickAndPlayApplication.class, args);
		System.out.println("[INFO] Swagger available at http://localhost:8080/swagger-ui/index.html");
	}
}
