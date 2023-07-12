package de.digitalcollections.cudami.external.controller.mets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.digitalcollections.cudami.external.repository.CudamiRepository;
import de.digitalcollections.cudami.external.service.mets.DfgMetsModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.junit.jupiter.api.Test;
import org.mycore.libmeta.mets.model.Mets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DfgMetsModsController.class)
class DfgMetsModsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CudamiRepository repoManager;
  @MockBean private DfgMetsModsService service;

  @Test
  void testGetDfgMetsModsByUuid() throws Exception {
    DigitalObject digitalObject = mock(DigitalObject.class);
    when(repoManager.getDigitalObject(any(DigitalObject.class))).thenReturn(digitalObject);

    Mets mets = mock(Mets.class);
    when(service.getMetsForDigitalObject(any(DigitalObject.class))).thenReturn(mets);

    String testUuid = "550e8400-e29b-11d4-a716-446655440000";
    this.mockMvc
        .perform(get("/mets/mods/v1/digitalobjects/" + testUuid + "/dfg"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());
  }
}
