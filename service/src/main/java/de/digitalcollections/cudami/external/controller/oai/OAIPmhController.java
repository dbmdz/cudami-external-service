package de.digitalcollections.cudami.external.controller.oai;

import de.digitalcollections.cudami.external.service.oai.OAIPmhServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
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
 * <p>see http://www.openarchives.org/OAI/openarchivesprotocol.html#ProtocolFeatures,
 * http://www.openarchives.org/OAI/openarchivesprotocol.html#ProtocolMessages,
 * http://www.openarchives.org/OAI/2.0/guidelines.htm,
 * http://www.openarchives.org/OAI/2.0/guidelines-repository.htm
 */
@RestController
public class OAIPmhController {

  private final OAIProvider oaiProvider;

  public OAIPmhController(OAIPmhServiceImpl oaiPmhService) {
    this.oaiProvider = new JAXBOAIProvider(oaiPmhService);
  }

  /**
   * All possible actions: see {@link OAIPmhServiceImpl}
   *
   * @param from an optional argument with a UTCdatetime value, which specifies a lower bound for
   *     datestamp-based selective harvesting, e.g. "1957-03-20"
   * @param identifier a required argument that specifies the unique identifier of the item in the
   *     repository from which the record must be disseminated (e.g. an UUID of a digital object)
   * @param metadataPrefix a required argument that specifies the metadataPrefix of the format that
   *     should be included in the metadata part of the returned record . A record should only be
   *     returned if the format specified by the metadataPrefix can be disseminated from the item
   *     identified by the value of the identifier argument. The metadata formats supported by a
   *     repository and for a particular record can be retrieved using the ListMetadataFormats
   *     request, e.g. "oai_dc"
   * @param resumptionToken an exclusive argument with a value that is the flow control token
   *     returned by a previous ListRecords request that issued an incomplete list. (something url
   *     encoded to be decoded on server side; must contain complete information for next
   *     request/paging). The OAI-PMH does not specify the syntax or even suggest an implementation
   *     strategy for resumptionToken elements, e.g. a base64 encoded params list
   * @param set an optional argument with a setSpec value , which specifies set criteria for
   *     selective harvesting, e.g. "handwritings"
   * @param until an optional argument with a UTCdatetime value, which specifies a upper bound for
   *     datestamp-based selective harvesting, e.g. "1957-03-30"
   * @param verb defines the action to be executed on server side (available verbs see
   *     specification)
   * @return response for requested verb and params
   * @throws Exception if server fails to fulfill request because of technical issues
   */
  @GetMapping(
      value = {"/oai/v2"},
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getByUuid(
      @Parameter(
              example = "1957-03-20T20:30:00Z or 1957-03-20",
              description =
                  "an optional argument with a UTCdatetime value, which specifies a lower bound for datestamp-based selective harvesting")
          @RequestParam(name = "from", required = false)
          String from,
      @Parameter(
              example = "4624c822-1d16-4bfa-9d10-3aa45cfab4aa",
              description =
                  "a required argument that specifies the unique identifier of the item in the repository from which the record must be disseminated")
          @RequestParam(name = "identifier", required = false)
          String identifier,
      @Parameter(
              example = "oai_dc",
              description =
                  "a required argument that specifies the metadataPrefix of the format that should be included in the metadata part of the returned record . A record should only be returned if the format specified by the metadataPrefix can be disseminated from the item identified by the value of the identifier argument. The metadata formats supported by a repository and for a particular record can be retrieved using the ListMetadataFormats request")
          @RequestParam(name = "metadataPrefix", required = false)
          String metadataPrefix,
      @Parameter(
              example = "9023A210CD007",
              description =
                  "an exclusive argument with a value that is the flow control token returned by a previous ListRecords request that issued an incomplete list. (something url encoded to be decoded on server side; must contain complete information for next request/paging). The OAI-PMH does not specify the syntax or even suggest an implementation strategy for resumptionToken elements")
          @RequestParam(name = "resumptionToken", required = false)
          String resumptionToken,
      @Parameter(
              example = "handwritings",
              description =
                  "an optional argument with a setSpec value , which specifies set criteria for selective harvesting")
          @RequestParam(name = "set", required = false)
          String set,
      @Parameter(
              example = "1957-03-21T20:30:00Z or 1957-03-21",
              description =
                  "an optional argument with a UTCdatetime value, which specifies a upper bound for datestamp-based selective harvesting")
          @RequestParam(name = "until", required = false)
          String until,
      @Parameter(
              example = "Identify",
              description =
                  "defines the action to be executed on server side (available verbs see specification)")
          @RequestParam(name = "verb", required = true)
          String verb)
      throws Exception {
    OAIRequest oaiRequest = new OAIRequest(verb);
    oaiRequest.setArgument(Argument.from, from);
    oaiRequest.setArgument(Argument.identifier, identifier);
    oaiRequest.setArgument(Argument.metadataPrefix, metadataPrefix);
    oaiRequest.setArgument(Argument.resumptionToken, resumptionToken);
    oaiRequest.setArgument(Argument.set, set);
    oaiRequest.setArgument(Argument.until, until);

    OAIResponse response = oaiProvider.handleRequest(oaiRequest);
    String xml = response.toString();
    return new ResponseEntity<String>(xml, HttpStatus.OK);
  }
}
