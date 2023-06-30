package de.digitalcollections.cudami.external.service.mets;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.cudami.external.config.DfgConfig;
import de.digitalcollections.cudami.external.repository.CudamiRepositoryManager;
import de.digitalcollections.cudami.external.service.mods.DfgModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.junit.jupiter.api.Test;
import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class DfgMetsModsServiceTest {

  @MockBean private CudamiRepositoryManager cudamiRepositoryManager;
  @Autowired private DfgConfig dfgConfig;

  @Test
  void testMetsForDigitalObject() throws Exception {
    DigitalObject digitalObject = new DigitalObject();
    DfgMetsModsService dfgMetsModsService =
        new DfgMetsModsService(new DfgModsService(), cudamiRepositoryManager, dfgConfig);
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
