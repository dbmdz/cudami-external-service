package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;

@Service
public class DfgModsService extends ModsService {
  @Override
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mods mods = super.getModsForDigitalObject(digitalObject);

    return mods;
  }
}
