package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;

public interface DfgModsService extends ModsService {

  @Override
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws ServiceException;
}
