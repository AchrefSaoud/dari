package utm.tn.dari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DariApplication {


	public static void main(String[] args) {

		 SpringApplication.run(DariApplication.class, args);
	}

}
