package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import org.mycore.libmeta.mets.model.Mets;

/** Service for creation of METS metadata */
public interface MetsService {

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws ServiceException;

  public Mets getFullCalendarMetsForManifestation(Manifestation manifestation)
      throws ServiceException;
}
