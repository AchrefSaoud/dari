package utm.tn.dari;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import utm.tn.dari.config.DockerElasticsearchLauncher;

@SpringBootApplication
@EnableAsync
public class DariApplication {


	public static void main(String[] args) {

		 SpringApplication.run(DariApplication.class, args);
	}

}
