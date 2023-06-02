package de.digitalcollections.cudami.external.service.oai;

import java.time.Instant;
import java.util.List;
import org.mycore.oai.pmh.BadResumptionTokenException;
import org.mycore.oai.pmh.CannotDisseminateFormatException;
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
    // TODO Auto-generated method stub
    return null;
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

  @Override
  public OAIDataList<? extends Record> getRecords(String resumptionToken)
      throws BadResumptionTokenException {
    // TODO Auto-generated method stub
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
