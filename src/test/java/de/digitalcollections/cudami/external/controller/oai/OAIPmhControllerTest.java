package de.digitalcollections.cudami.external.controller.oai;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.digitalcollections.cudami.external.service.oai.OAIPmhService;
import org.junit.jupiter.api.Test;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Identify.DeletedRecordPolicy;
import org.mycore.oai.pmh.SimpleIdentify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OAIPmhController.class)
class OAIPmhControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private OAIPmhService service;

  @Test
  void testMissingVerb() throws Exception {
    this.mockMvc
        .perform(get("/oai"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(status().reason("Required parameter 'verb' is not present."));
  }

  @Test
  void testIdentify() throws Exception {
    SimpleIdentify identify = new SimpleIdentify();
    identify.setGranularity(Granularity.YYYY_MM_DD_THH_MM_SS_Z);
    identify.setDeletedRecordPolicy(
        DeletedRecordPolicy.No); // the repository does not maintain information about
    // deletions. A repository that indicates this level of
    // support must not reveal a deleted status in any
    // response.
    when(service.getIdentify()).thenReturn(identify);

    this.mockMvc
        .perform(get("/oai?verb=Identify"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(containsString("<protocolVersion>2.0</protocolVersion>")))
        .andExpect(
            content().string(containsString("<granularity>YYYY-MM-DDThh:mm:ssZ</granularity>")))
        .andExpect(content().string(containsString("<deletedRecord>no</deletedRecord>")));
  }
}
