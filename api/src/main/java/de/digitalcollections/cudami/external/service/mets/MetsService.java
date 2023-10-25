package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.Mets;

/** Service for creation of METS metadata */
public interface MetsService {

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws ServiceException;

  public Mets getMetsForFullCalendar(Identifier identifier) throws ServiceException;
}
