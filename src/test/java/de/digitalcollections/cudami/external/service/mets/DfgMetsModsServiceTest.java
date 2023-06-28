package de.digitalcollections.cudami.external.service.mets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.digitalcollections.cudami.external.repository.CudamiRepositoryManager;
import de.digitalcollections.cudami.external.service.mods.DfgModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DfgMetsModsServiceTest {

  @MockBean private CudamiRepositoryManager cudamiRepositoryManager;

  @Test
  @Disabled
  void testMetsForDigitalObject() throws Exception {
    DigitalObject digitalObject = new DigitalObject();
    DfgMetsModsService dfgMetsModsService =
        new DfgMetsModsService(new DfgModsService(), cudamiRepositoryManager);
    Mets mets = dfgMetsModsService.getMetsForDigitalObject(digitalObject);
    String actual = METSXMLProcessor.getInstance().marshalToString(mets);
    String expected = "";
    assertEquals(expected, actual);
  }
}
