package de.digitalcollections.cudami.external;

import de.digitalcollections.cudami.external.config.SpringConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ConfigurationPropertiesScan
@TestPropertySource("classpath:application.yml")
@Import(SpringConfig.class)
class ApplicationTests {

  @Test
  void contextLoads() {}
}
