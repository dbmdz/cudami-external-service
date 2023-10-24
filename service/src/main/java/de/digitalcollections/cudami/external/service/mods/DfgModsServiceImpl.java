package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;

/**
 * https://dfg-viewer.de/fileadmin/groups/dfgviewer/MODS-Anwendungsprofil_2.3.1.pdf
 *
 * <p>Grundlage für dieses Anwendungsprofil ist das Metadata Object Description Schema(MODS) in der
 * Version 3.5, das von der Library of Congress gepflegt wird
 */
@Service("DfgModsServiceImpl")
public class DfgModsServiceImpl implements DfgModsService {

  private ModsService modsService;

  public DfgModsServiceImpl(ModsService modsService) {
    this.modsService = modsService;
  }

  @Override
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws ServiceException {
    Mods mods = modsService.getModsForDigitalObject(digitalObject);
    return mods;
  }
}
