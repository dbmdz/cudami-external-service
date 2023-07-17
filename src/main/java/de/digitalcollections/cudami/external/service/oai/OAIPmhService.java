package de.digitalcollections.cudami.external.service.oai;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.digitalcollections.cudami.external.repository.CudamiRepository;
import de.digitalcollections.cudami.external.service.mets.DfgMetsModsService;
import de.digitalcollections.model.identifiable.entity.Collection;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.jackson.DigitalCollectionsObjectMapper;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.list.sorting.Direction;
import de.digitalcollections.model.list.sorting.Order;
import de.digitalcollections.model.list.sorting.Sorting;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.oai.pmh.BadResumptionTokenException;
import org.mycore.oai.pmh.CannotDisseminateFormatException;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.IdDoesNotExistException;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.Identify.DeletedRecordPolicy;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.NoMetadataFormatsException;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.NoSetHierarchyException;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.ResumptionToken;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.SimpleIdentify;
import org.mycore.oai.pmh.SimpleMetadata;
import org.mycore.oai.pmh.SimpleResumptionToken;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * see example implementation at
 * https://github.com/MyCoRe-Org/oaipmh/blob/main/oaipmh-dataprovider-impl/src/test/java/org/mycore/oai/pmh/dataprovider/impl/SimpleOAIAdapter.java
 */
@Service
public class OAIPmhService implements OAIAdapter {

  public static class PageRequestWrapper {
    private PageRequest pageRequest;
    private Set set;

    public PageRequestWrapper() {}

    public PageRequest getPageRequest() {
      return pageRequest;
    }

    public Set getSet() {
      return set;
    }

    public void setPageRequest(PageRequest pageRequest) {
      this.pageRequest = pageRequest;
    }

    public void setSet(Set set) {
      this.set = set;
    }
  }

  private final CudamiRepository cudamiRepository;
  private final DfgMetsModsService dfgMetsModsService;
  private final DigitalCollectionsObjectMapper objectMapper;

  public OAIPmhService(
      CudamiRepository cudamiRepository,
      DigitalCollectionsObjectMapper objectMapper,
      DfgMetsModsService dfgMetsModsService) {
    this.cudamiRepository = cudamiRepository;
    this.objectMapper = objectMapper;
    this.dfgMetsModsService = dfgMetsModsService;
  }

  /**
   * Verb "ListIdentifiers" is an abbreviated form of ListRecords, retrieving only headers rather
   * than records. Optional arguments permit selective harvesting of headers based on set membership
   * and/or datestamp. Depending on the repository's support for deletions, a returned header may
   * have a status attribute of "deleted" if a record matching the arguments specified in the
   * request has been deleted.
   *
   * <p>Arguments
   *
   * <p>"from" - an optional argument with a UTCdatetime value, which specifies a lower bound for
   * datestamp-based selective harvesting.
   *
   * <p>"until" - an optional argument with a UTCdatetime value, which specifies a upper bound for
   * datestamp-based selective harvesting.
   *
   * <p>"metadataPrefix" - a required argument, which specifies that headers should be returned only
   * if the metadata format matching the supplied metadataPrefix is available or, depending on the
   * repository's support for deletions, has been deleted. The metadata formats supported by a
   * repository and for a particular item can be retrieved using the ListMetadataFormats request.
   *
   * <p>"set" - an optional argument with a setSpec value , which specifies set criteria for
   * selective harvesting.
   *
   * <p>"resumptionToken" - an exclusive argument with a value that is the flow control token
   * returned by a previous ListIdentifiers request that issued an incomplete list.
   *
   * <p>Error and Exception Conditions
   *
   * <p>"badArgument" - The request includes illegal arguments or is missing required arguments.
   *
   * <p>"badResumptionToken" - The value of the resumptionToken argument is invalid or expired.
   *
   * <p>"cannotDisseminateFormat" - The value of the metadataPrefix argument is not supported by the
   * repository.
   *
   * <p>"noRecordsMatch" - The combination of the values of the from, until, and set arguments
   * results in an empty list.
   *
   * <p>"noSetHierarchy" - The repository does not support sets.
   */
  @Override
  public OAIDataList<? extends Header> getHeaders(
      MetadataFormat format, Set set, Instant from, Instant until)
      throws CannotDisseminateFormatException, NoSetHierarchyException, NoRecordsMatchException {
    Sorting sorting =
        Sorting.builder()
            .order(Order.builder().direction(Direction.ASC).property("lastModified").build())
            .build();

    // TODO: filtering for from, until

    PageRequest.Builder pageRequestBuilder = PageRequest.builder();
    pageRequestBuilder
        .pageNumber(0)
        .pageSize(100) // list contains only minimal data, so 100 is no problem
        .sorting(sorting)
        .build();
    PageRequest pageRequest = pageRequestBuilder.build();

    PageRequestWrapper pageRequestWrapper = new PageRequestWrapper();
    pageRequestWrapper.setSet(set);
    pageRequestWrapper.setPageRequest(pageRequest);
    return getHeaders(pageRequestWrapper);
  }

  private OAIDataList<? extends Header> getHeaders(PageRequestWrapper pageRequestWrapper) {
    Set set = pageRequestWrapper.getSet();
    PageRequest pageRequest = pageRequestWrapper.getPageRequest();

    PageResponse<DigitalObject> pageResponse;
    if (set == null) {
      pageResponse = cudamiRepository.findDigitalObjects(pageRequest);
    } else {
      UUID collectionUuid = UUID.fromString(set.getSpec());
      pageResponse = cudamiRepository.findDigitalObjectsOfCollection(collectionUuid, pageRequest);
    }
    OAIDataList<Header> result = new OAIDataList<>();
    if (pageResponse.hasContent()) {
      List<DigitalObject> content = pageResponse.getContent();
      for (DigitalObject digitalObject : content) {
        // id spec: http://www.openarchives.org/OAI/openarchivesprotocol.html#UniqueIdentifier
        String id = "oai:" + digitalObject.getUuid().toString();
        Instant datestamp = digitalObject.getLastModified().toInstant(ZoneOffset.UTC);
        Header header = new Header(id, datestamp);
        result.add(header);
      }
    }

    if (pageResponse.hasNext()) {
      // set resumptiontoken
      PageRequest nextPageRequest = pageRequest.next();
      PageRequestWrapper nextPageRequestWrapper = new PageRequestWrapper();
      nextPageRequestWrapper.setSet(set);
      nextPageRequestWrapper.setPageRequest(nextPageRequest);
      ResumptionToken resumptionToken =
          resumptionTokenFromPageRequestWrapper(nextPageRequestWrapper);
      result.setResumptionToken(resumptionToken);
    }
    return result;
  }

  /** see {@link #getHeaders(MetadataFormat, Set, Instant, Instant)} */
  @Override
  public OAIDataList<? extends Header> getHeaders(String resumptionToken)
      throws BadResumptionTokenException {
    PageRequestWrapper pageRequestWrapper = resumptionTokenToPageRequestWrapper(resumptionToken);
    return getHeaders(pageRequestWrapper);
  }

  /**
   * Verb "Identify" is used to retrieve information about a repository. Some of the information
   * returned is required as part of the OAI-PMH. Repositories may also employ the Identify verb to
   * return additional descriptive information.
   *
   * <p>Arguments: none
   *
   * <p>Error and Exception Conditions:
   *
   * <p>badArgument - The request includes illegal arguments.
   *
   * <p>Response Format
   *
   * <p>The response must include one instance of the following elements:
   *
   * <p>repositoryName : a human readable name for the repository;
   *
   * <p>baseURL : the base URL of the repository;
   *
   * <p>protocolVersion : the version of the OAI-PMH supported by the repository;
   *
   * <p>earliestDatestamp : a UTCdatetime that is the guaranteed lower limit of all datestamps
   * recording changes, modifications, or deletions in the repository. A repository must not use
   * datestamps lower than the one specified by the content of the earliestDatestamp element.
   * earliestDatestamp must be expressed at the finest granularity supported by the repository.
   *
   * <p>deletedRecord : the manner in which the repository supports the notion of deleted records.
   * Legitimate values are no ; transient ; persistent with meanings defined in the section on
   * deletion.
   *
   * <p>granularity: the finest harvesting granularity supported by the repository. The legitimate
   * values are YYYY-MM-DD and YYYY-MM-DDThh:mm:ssZ with meanings as defined in ISO8601.
   *
   * <p>The response must include one or more instances of the following element:
   *
   * <p>adminEmail : the e-mail address of an administrator of the repository.
   *
   * <p>The response may include multiple instances of the following optional elements:
   *
   * <p>compression : a compression encoding supported by the repository. The recommended values are
   * those defined for the Content-Encoding header in Section 14.11 of RFC 2616 describing HTTP 1.1.
   * A compression element should not be included for the identity encoding, which is implied.
   *
   * <p>description : an extensible mechanism for communities to describe their repositories. For
   * example, the description container could be used to include collection-level metadata in the
   * response to the Identify request. Implementation Guidelines are available to give directions
   * with this respect. Each description container must be accompanied by the URL of an XML schema
   * describing the structure of the description container.
   */
  @Override
  public Identify getIdentify() {
    SimpleIdentify identify = new SimpleIdentify();
    identify.setGranularity(
        Granularity.YYYY_MM_DD_THH_MM_SS_Z); // cudami model provides lastModified as timestamp....
    identify.setDeletedRecordPolicy(
        DeletedRecordPolicy.No); // the repository does not maintain information about
    // deletions. A repository that indicates this level of
    // support must not reveal a deleted status in any
    // response.
    return identify;
  }

  /** needed by JAXBOAIProvider */
  @Override
  public MetadataFormat getMetadataFormat(String prefix) throws CannotDisseminateFormatException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Verb "ListMetadataFormats" is used to retrieve the metadata formats available from a
   * repository. An optional argument restricts the request to the formats available for a specific
   * item.
   *
   * <p>Arguments
   *
   * <p>"identifier" - an optional argument that specifies the unique identifier of the item for
   * which available metadata formats are being requested. If this argument is omitted, then the
   * response includes all metadata formats supported by this repository. Note that the fact that a
   * metadata format is supported by a repository does not mean that it can be disseminated from all
   * items in the repository.
   *
   * <p>Error and Exception Conditions
   *
   * <p>"badArgument" - The request includes illegal arguments or is missing required arguments.
   *
   * <p>"idDoesNotExist" - The value of the identifier argument is unknown or illegal in this
   * repository.
   *
   * <p>"noMetadataFormats" - There are no metadata formats available for the specified item.
   */
  @Override
  public List<? extends MetadataFormat> getMetadataFormats() {
    // TODO Auto-generated method stub
    return null;
  }

  /** see {@link #getMetadataFormats()} */
  @Override
  public List<? extends MetadataFormat> getMetadataFormats(String identifier)
      throws IdDoesNotExistException, NoMetadataFormatsException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Verb "GetRecord" is used to retrieve an individual metadata record from a repository.
   *
   * <p>Required arguments specify the identifier of the item from which the record is requested and
   * the format of the metadata that should be included in the record.
   *
   * <p>Depending on the level at which a repository tracks deletions, a header with a "deleted"
   * value for the status attribute may be returned, in case the metadata format specified by the
   * metadataPrefix is no longer available from the repository or from the specified item.
   *
   * <p>Arguments:
   *
   * <p>"identifier" - a required argument that specifies the unique identifier of the item in the
   * repository from which the record must be disseminated.
   *
   * <p>"metadataPrefix" - a required argument that specifies the metadataPrefix of the format that
   * should be included in the metadata part of the returned record. A record should only be
   * returned if the format specified by the metadataPrefix can be disseminated from the item
   * identified by the value of the identifier argument. The metadata formats supported by a
   * repository and for a particular record can be retrieved using the ListMetadataFormats request.
   *
   * <p>Error and Exception Conditions:
   *
   * <p>"badArgument" - The request includes illegal arguments or is missing required arguments.
   *
   * <p>"cannotDisseminateFormat" - The value of the metadataPrefix argument is not supported by the
   * item identified by the value of the identifier argument.
   *
   * <p>"idDoesNotExist" - The value of the identifier argument is unknown or illegal in this
   * repository.
   */
  @Override
  public Record getRecord(String identifier, MetadataFormat format)
      throws CannotDisseminateFormatException, IdDoesNotExistException {
    Record result = null;

    UUID digitalObjectUuid = null;
    // cut uuid from identifier (may be even longer/more parts in the end...)
    // see bdr: "identifier=oai:bdr.oai.bsb-muenchen.de:all:BDR-BV000036054-63455"
    if (identifier.startsWith("oai:")) {
      String uuidStr = identifier.substring(identifier.lastIndexOf(":") + 1);
      digitalObjectUuid = UUID.fromString(uuidStr);
    }

    if (digitalObjectUuid != null) {
      DigitalObject digitalObject =
          cudamiRepository.getDigitalObject(
              DigitalObject.builder().uuid(digitalObjectUuid).build());
      try {
        // TODO: support oai_dc/format declared in MetadataFormat
        Mets mets = dfgMetsModsService.getMetsForDigitalObject(digitalObject);
        // id spec: http://www.openarchives.org/OAI/openarchivesprotocol.html#UniqueIdentifier
        String id = "oai:" + digitalObject.getUuid().toString();
        Instant datestamp = digitalObject.getLastModified().toInstant(ZoneOffset.UTC);
        Header header = new Header(id, datestamp);

        result = new Record(header);
        Document document = METSXMLProcessor.getInstance().marshalToDOM(mets);
        // convert w3c element to jdom element:
        DOMBuilder builder = new DOMBuilder();
        Element element = builder.build(document.getDocumentElement());
        result.setMetadata(new SimpleMetadata(element));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Verb "ListRecords" is used to harvest records from a repository. Optional arguments permit
   * selective harvesting of records based on set membership and/or datestamp. Depending on the
   * repository's support for deletions, a returned header may have a status attribute of "deleted"
   * if a record matching the arguments specified in the request has been deleted. No metadata will
   * be present for records with deleted status.
   *
   * <p>Arguments
   *
   * <p>"from" - an optional argument with a UTCdatetime value, which specifies a lower bound for
   * datestamp-based selective harvesting.
   *
   * <p>"until" - an optional argument with a UTCdatetime value, which specifies a upper bound for
   * datestamp-based selective harvesting.
   *
   * <p>"set" - an optional argument with a setSpec value , which specifies set criteria for
   * selective harvesting.
   *
   * <p>"resumptionToken" - an exclusive argument with a value that is the flow control token
   * returned by a previous ListRecords request that issued an incomplete list.
   *
   * <p>"metadataPrefix" - a required argument (unless the exclusive argument resumptionToken is
   * used) that specifies the metadataPrefix of the format that should be included in the metadata
   * part of the returned records. Records should be included only for items from which the metadata
   * format matching the metadataPrefix can be disseminated. The metadata formats supported by a
   * repository and for a particular item can be retrieved using the ListMetadataFormats request.
   *
   * <p>Error and Exception Conditions
   *
   * <p>"badArgument" - The request includes illegal arguments or is missing required arguments.
   *
   * <p>"badResumptionToken" - The value of the resumptionToken argument is invalid or expired.
   *
   * <p>"cannotDisseminateFormat" - The value of the metadataPrefix argument is not supported by the
   * repository.
   *
   * <p>"noRecordsMatch" - The combination of the values of the from, until, set and metadataPrefix
   * arguments results in an empty list.
   *
   * <p>"noSetHierarchy" - The repository does not support sets.
   */
  @Override
  public OAIDataList<? extends Record> getRecords(
      MetadataFormat format, Set set, Instant from, Instant until)
      throws CannotDisseminateFormatException, NoSetHierarchyException, NoRecordsMatchException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * See {@link #getRecords(MetadataFormat, Set, Instant, Instant)}
   *
   * <p>resumptionToken: see http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl
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

  /** needed by JAXBOAIProvider */
  @Override
  public Set getSet(String setSpec) throws NoSetHierarchyException, NoRecordsMatchException {
    Collection collection = cudamiRepository.getCollection(setSpec);

    if (collection != null) {
      String id = collection.getUuid().toString();
      String name = collection.getLabel().getText();
      return new Set(id, name);
    }

    throw new NoRecordsMatchException("No collection for UUID " + setSpec + " found.");
  }

  /**
   * Verb "ListSets" is used to retrieve the set structure of a repository, useful for selective
   * harvesting.
   *
   * <p>Arguments
   *
   * <p>"resumptionToken" - an exclusive argument with a value that is the flow control token
   * returned by a previous ListSets request that issued an incomplete list.
   *
   * <p>Error and Exception Conditions
   *
   * <p>"badArgument" - The request includes illegal arguments or is missing required arguments.
   *
   * <p>"badResumptionToken" - The value of the resumptionToken argument is invalid or expired.
   *
   * <p>"noSetHierarchy" - The repository does not support sets.
   */
  @Override
  public OAIDataList<? extends Set> getSets() throws NoSetHierarchyException {
    Sorting sorting =
        Sorting.builder()
            .order(Order.builder().direction(Direction.ASC).property("lastModified").build())
            .build();

    PageRequest.Builder pageRequestBuilder = PageRequest.builder();
    pageRequestBuilder
        .pageNumber(0)
        .pageSize(100) // list contains only minimal data, so 100 is no problem
        .sorting(sorting)
        .build();
    PageRequest pageRequest = pageRequestBuilder.build();

    return getSets(pageRequest);
  }

  private OAIDataList<? extends Set> getSets(PageRequest pageRequest) {
    PageResponse<Collection> pageResponse = cudamiRepository.findCollections(pageRequest);

    OAIDataList<Set> result = new OAIDataList<>();
    if (pageResponse.hasContent()) {
      List<Collection> content = pageResponse.getContent();
      for (Collection collection : content) {
        String id = collection.getUuid().toString();
        String name = collection.getLabel().getText();
        Set set = new Set(id, name);
        result.add(set);
      }
    }

    if (pageResponse.hasNext()) {
      // set resumptiontoken
      PageRequest nextPageRequest = pageRequest.next();
      ResumptionToken resumptionToken = resumptionTokenFromPageRequest(nextPageRequest);
      result.setResumptionToken(resumptionToken);
    }
    return result;
  }

  /** See {@link #getSets()} */
  @Override
  public OAIDataList<? extends Set> getSets(String resumptionToken)
      throws NoSetHierarchyException, BadResumptionTokenException {
    PageRequest pageRequest = resumptionTokenToPageRequest(resumptionToken);
    return getSets(pageRequest);
  }

  private ResumptionToken resumptionTokenFromPageRequest(PageRequest pageRequest) {
    ResumptionToken result = null;
    try {
      byte[] pageRequestWrapperBytes = objectMapper.writeValueAsBytes(pageRequest);
      String token = Base64.getEncoder().encodeToString(pageRequestWrapperBytes);
      result = new SimpleResumptionToken(token);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  private ResumptionToken resumptionTokenFromPageRequestWrapper(
      PageRequestWrapper pageRequestWrapper) {
    ResumptionToken result = null;
    try {
      byte[] pageRequestBytes = objectMapper.writeValueAsBytes(pageRequestWrapper);
      String token = Base64.getEncoder().encodeToString(pageRequestBytes);
      result = new SimpleResumptionToken(token);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  private PageRequest resumptionTokenToPageRequest(String resumptionToken) {
    PageRequest result = null;
    try {
      byte[] token = Base64.getDecoder().decode(resumptionToken);
      result = objectMapper.readValue(token, PageRequest.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  private PageRequestWrapper resumptionTokenToPageRequestWrapper(String resumptionToken) {
    PageRequestWrapper result = null;
    try {
      byte[] token = Base64.getDecoder().decode(resumptionToken);
      result = objectMapper.readValue(token, PageRequestWrapper.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }
}
