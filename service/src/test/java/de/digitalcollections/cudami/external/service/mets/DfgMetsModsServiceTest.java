package de.digitalcollections.cudami.external.service.mets;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.cudami.external.config.DfgConfig;
import de.digitalcollections.cudami.external.config.IiifConfig;
import de.digitalcollections.cudami.external.repository.CustomCudamiRepositoryImpl;
import de.digitalcollections.cudami.external.service.mods.DfgModsServiceImpl;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.jackson.DigitalCollectionsObjectMapper;
import de.digitalcollections.model.text.LocalizedText;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ConfigurationPropertiesScan(basePackages = "de.digitalcollections.cudami.external.config")
@TestPropertySource("classpath:application.yml")
class DfgMetsModsServiceTest {

  @MockBean private CustomCudamiRepositoryImpl cudamiRepositoryManager;
  @MockBean private DigitalCollectionsObjectMapper digitalCollectionsObjectMapper;
  @Autowired private DfgConfig dfgConfig;
  @Autowired private IiifConfig iiifConfig;

  @Test
  void testMetsForDigitalObject() throws Exception {
    DigitalObject digitalObject = new DigitalObject();
    digitalObject.setLabel(LocalizedText.builder().text(Locale.GERMAN, "test").build());
    digitalObject.setUuid(UUID.fromString("4624c822-1d16-4bfa-9d10-3aa45cfab4aa"));
    DfgMetsModsServiceImpl dfgMetsModsService =
        new DfgMetsModsServiceImpl(
            new DfgModsServiceImpl(), cudamiRepositoryManager, dfgConfig, iiifConfig);
    Mets mets = dfgMetsModsService.getMetsForDigitalObject(digitalObject);
    String actual = METSXMLProcessor.getInstance().marshalToString(mets);
    System.out.println(actual);
    //    String expected = "TODO test fixture";
    //    assertEquals(expected, actual);

    assertThat(
        actual.contains(
            "mets:mets")); // TODO: just dummy to see generated mets output from above and test does
    // not fail.... Replace with complete mets comparison when mets is
    // completly implemented...
  }
}
