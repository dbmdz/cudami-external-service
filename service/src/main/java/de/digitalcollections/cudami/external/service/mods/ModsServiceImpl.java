package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mods.model.Mods;
import org.mycore.libmeta.mods.model.ModsVersion;
import org.mycore.libmeta.mods.model._toplevel.*;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsServiceImpl implements ModsService {

  private ModsItemsService modsItemsService;

  public ModsServiceImpl(ModsItemsService modsItemsService) {
    this.modsItemsService = modsItemsService;
  }

  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws ServiceException {

    // mods:location
    Location location = modsItemsService.createLocation(digitalObject);

    // mods:relatedItem
    RelatedItem relatedItem = modsItemsService.createRelatedItem(digitalObject);

    // mods:physicalDescription
    PhysicalDescription physicalDescription =
        modsItemsService.createPhysicalDescription(digitalObject);

    // mods:identifier type="purl"
    Identifier identifierPurl = modsItemsService.createIdentifierPurl(digitalObject);

    // mods:identifier type="urn"
    Identifier identifierUrn = modsItemsService.createIdentifierUrn(digitalObject);

    // mods:originInfo
    OriginInfo originInfo = modsItemsService.createOriginInfo(digitalObject);

    Mods mods =
        Mods.builder()
            .version(ModsVersion.VERSION_3_5)
            .addContent(location)
            .addContent(relatedItem)
            .addContent(physicalDescription)
            .addContent(identifierPurl)
            .addContent(identifierUrn)
            .addContent(originInfo)
            .build();
    return mods;
  }
}
