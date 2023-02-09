package in.co.bel.ims.initial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import in.co.bel.ims.initial.service.util.ExternalPassConfigReader;

@SpringBootApplication
@EnableConfigurationProperties(ExternalPassConfigReader.class)
public class ImsDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImsDataApplication.class, args);
	}

}
