package de.digitalcollections.cudami.external.service.oai;

import java.time.Instant;
import java.util.List;
import org.mycore.oai.pmh.BadResumptionTokenException;
import org.mycore.oai.pmh.CannotDisseminateFormatException;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.IdDoesNotExistException;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.NoMetadataFormatsException;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.NoSetHierarchyException;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.SimpleIdentify;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.springframework.stereotype.Service;

/**
 * see example implementation at
 * https://github.com/MyCoRe-Org/oaipmh/blob/main/oaipmh-dataprovider-impl/src/test/java/org/mycore/oai/pmh/dataprovider/impl/SimpleOAIAdapter.java
 */
@Service
public class OAIPmhService implements OAIAdapter {

  @Override
  public Identify getIdentify() {
    SimpleIdentify identify = new SimpleIdentify();
    identify.setGranularity(
        Granularity.YYYY_MM_DD_THH_MM_SS_Z); // cudami model provides lastModified as timestamp....
    return identify;
  }

  @Override
  public OAIDataList<? extends Set> getSets() throws NoSetHierarchyException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OAIDataList<? extends Set> getSets(String resumptionToken)
      throws NoSetHierarchyException, BadResumptionTokenException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set getSet(String setSpec) throws NoSetHierarchyException, NoRecordsMatchException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<? extends MetadataFormat> getMetadataFormats() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MetadataFormat getMetadataFormat(String prefix) throws CannotDisseminateFormatException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<? extends MetadataFormat> getMetadataFormats(String identifier)
      throws IdDoesNotExistException, NoMetadataFormatsException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Record getRecord(String identifier, MetadataFormat format)
      throws CannotDisseminateFormatException, IdDoesNotExistException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * resumptionToken: see http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl
   *
   * <ul>
   *   <li>The only defined use of resumptionToken is as follows:
   *       <ul>
   *         <li>a repository must include a resumptionToken element as part of each response that
   *             includes an incomplete list;
   *         <li>in order to retrieve the next portion of the complete list, the next request must
   *             use the value of that resumptionToken element as the value of the resumptionToken
   *             argument of the request;
   *         <li>the response containing the incomplete list that completes the list must include an
   *             empty resumptionToken element;
   *       </ul>
   *       All other uses of resumptionToken by a harvester are illegal and must return an error.
   *   <li>In all cases when a resumptionToken is issued, the incomplete list must consist of
   *       complete entities; e.g., all individual records returned in an incomplete record list
   *       from a ListRecords request must be intact.
   *   <li>The format of the resumptionToken is not defined by the OAI-PMH and should be considered
   *       opaque by the harvester.
   *   <li>The protocol does not define the semantics of incompleteness. Therefore, a harvester
   *       should not assume that the members in an incomplete list conform to some selection
   *       criteria (e.g., date ordering).
   *   <li>Before including a resumptionToken in the URL of a subsequent request, a harvester must
   *       encode any special characters in it.
   * </ul>
   */
  @Override
  public OAIDataList<? extends Record> getRecords(String resumptionToken)
      throws BadResumptionTokenException {
    // TODO decode resumptionToken and extract PageRequest with filtering from it; issue PageRequest
    // for next page
    return null;
  }

  @Override
  public OAIDataList<? extends Record> getRecords(
      MetadataFormat format, Set set, Instant from, Instant until)
      throws CannotDisseminateFormatException, NoSetHierarchyException, NoRecordsMatchException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OAIDataList<? extends Header> getHeaders(String resumptionToken)
      throws BadResumptionTokenException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OAIDataList<? extends Header> getHeaders(
      MetadataFormat format, Set set, Instant from, Instant until)
      throws CannotDisseminateFormatException, NoSetHierarchyException, NoRecordsMatchException {
    // TODO Auto-generated method stub
    return null;
  }
}
