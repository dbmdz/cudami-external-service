package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;

/**
 * https://dfg-viewer.de/fileadmin/groups/dfgviewer/MODS-Anwendungsprofil_2.3.1.pdf
 *
 * <p>Grundlage für dieses Anwendungsprofil ist das Metadata Object Description Schema(MODS) in der
 * Version 3.5, das von der Library of Congress gepflegt wird
 */
@Service
public class DfgModsService extends ModsService {
  @Override
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mods mods = super.getModsForDigitalObject(digitalObject);
    return mods;
  }
}
