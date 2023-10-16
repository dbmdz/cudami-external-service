package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;

public interface ModsService {

  /**
   * Creates the Mods data for a DigitalObject
   * @param digitalObject the DigitalObject
   * @return Mods data
   * @throws ServiceException in case of an error
   */
  Mods getModsForDigitalObject(DigitalObject digitalObject) throws ServiceException;
}
