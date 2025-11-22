package az.edu.itbrains.food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Bu sətir vacibdir

public class FoodApplication {

	public static void main(String[] args) {

        SpringApplication.run(FoodApplication.class, args);
	}

}
