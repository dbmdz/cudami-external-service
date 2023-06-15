package de.digitalcollections.cudami.external;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ConfigurationPropertiesScan
@TestPropertySource("classpath:application.yml")
class ApplicationTests {

  @Test
  void contextLoads() {}
}
