package de.digitalcollections.cudami.external;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"de.digitalcollections.cudami.external"})
@ConfigurationPropertiesScan
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
