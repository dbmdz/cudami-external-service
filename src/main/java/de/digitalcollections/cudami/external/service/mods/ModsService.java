package de.digitalcollections.cudami.external.service.mods;

import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;

/**
 * Service for creation of METS metadata by given (fully filled) DigitalObject.
 */
@Service
public class ModsService {
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mods mods = new Mods();
    return mods;
  }
}
