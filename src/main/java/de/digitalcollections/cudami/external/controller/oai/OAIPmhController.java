package de.digitalcollections.cudami.external.controller.oai;

import de.digitalcollections.cudami.external.service.oai.OAIPmhService;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.dataprovider.OAIProvider;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.mycore.oai.pmh.dataprovider.impl.JAXBOAIProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAI-PMH endpoint fulfilling http://www.openarchives.org/OAI/openarchivesprotocol.html
 *
 * <p>see http://www.openarchives.org/OAI/openarchivesprotocol.html#ProtocolFeatures
 * http://www.openarchives.org/OAI/openarchivesprotocol.html#ProtocolMessages
 */
@RestController
public class OAIPmhController {

  private final OAIProvider oaiProvider;

  public OAIPmhController(OAIPmhService oaiPmhService) {
    this.oaiProvider = new JAXBOAIProvider(oaiPmhService);
  }

  @GetMapping(
      value = {"/oai"},
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getByUuid(
      @RequestParam(name = "verb", required = true) String verb,
      @RequestParam(name = "metadataPrefix", required = false) String metadataPrefix)
      // TODO all other params
      throws Exception {
    OAIRequest oaiRequest = new OAIRequest(verb);
    oaiRequest.setArgument(Argument.metadataPrefix, metadataPrefix);
    // TODO ...

    OAIResponse response = oaiProvider.handleRequest(oaiRequest);
    String xml = response.toString();
    return new ResponseEntity<String>(xml, HttpStatus.OK);
  }
}
