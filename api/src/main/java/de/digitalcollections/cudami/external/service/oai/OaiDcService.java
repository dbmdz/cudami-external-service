package de.digitalcollections.cudami.external.service.oai;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.oaidc.model.OaiDc;

public interface OaiDcService {

  /**
   * Create Dublin Core OAI for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return OaiDC object
   * @throws ServiceException in case of an error
   */
  public OaiDc getOaiDcForDigitalObject(DigitalObject digitalObject) throws ServiceException;
}
