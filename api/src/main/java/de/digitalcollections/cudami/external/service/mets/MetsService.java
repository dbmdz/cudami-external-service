package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.Mets;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
public interface MetsService {

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws ServiceException;
}
