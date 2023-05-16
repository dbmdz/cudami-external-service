package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import org.mycore.libmeta.mods.model.Mods;
import org.mycore.libmeta.mods.model._misc.enums.RelatedItemType;
import org.mycore.libmeta.mods.model._toplevel.Identifier;
import org.mycore.libmeta.mods.model._toplevel.Location;
import org.mycore.libmeta.mods.model._toplevel.OriginInfo;
import org.mycore.libmeta.mods.model._toplevel.PhysicalDescription;
import org.mycore.libmeta.mods.model._toplevel.RelatedItem;
import org.mycore.libmeta.mods.model.location.ShelfLocator;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsService {
  protected Identifier createIdentifierPurl(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("purl").build();
    return identifier;
  }

  protected Identifier createIdentifierUrn(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("urn").build();
    return identifier;
  }

  protected Location createLocation(DigitalObject digitalObject) {
    // mods:shelfLocator
    ShelfLocator shelfLocator = createShelfLocator(digitalObject);

    Location location = Location.builderForLocation().addShelfLocator(shelfLocator).build();
    return location;
  }

  protected OriginInfo createOriginInfo(DigitalObject digitalObject) {
    // mods:place
    // TODO

    // mods:dateIssued
    // TODO

    OriginInfo originInfo = OriginInfo.builderForOriginInfo().build();
    return originInfo;
  }

  protected PhysicalDescription createPhysicalDescription(DigitalObject digitalObject) {
    // TODO use DigitalOrigin.REFORMATTED_DIGITAL
    // see https://github.com/MyCoRe-Org/libmeta/issues/5
    // PhysicalDescription physicalDescription =
    //   PhysicalDescription.builderForPhysicalDescription().addContent(???).build();
    PhysicalDescription physicalDescription =
        PhysicalDescription.builderForPhysicalDescription().build();
    return physicalDescription;
  }

  protected RelatedItem createRelatedItem(DigitalObject digitalObject) {
    // mods:titleInfo
    // TODO

    // mods:language
    // TODO

    // mods:recordInfo
    // TODO

    RelatedItem relatedItem =
        RelatedItem.builderForRelatedItem().type(RelatedItemType.HOST).build();
    return relatedItem;
  }

  protected ShelfLocator createShelfLocator(DigitalObject digitalObject) {
    ShelfLocator shelfLocator = null;
    Item item = digitalObject.getItem();
    de.digitalcollections.model.identifiable.Identifier shelfNo =
        item.getIdentifierByNamespace("shelfno");
    if (shelfNo != null) {
      shelfLocator = ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
    }
    return shelfLocator;
  }

  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {

    // mods:location
    Location location = createLocation(digitalObject);

    // mods:relatedItem
    RelatedItem relatedItem = createRelatedItem(digitalObject);

    // mods:physicalDescription
    PhysicalDescription physicalDescription = createPhysicalDescription(digitalObject);

    // mods:identifier type="purl"
    Identifier identifierPurl = createIdentifierPurl(digitalObject);

    // mods:identifier type="urn"
    Identifier identifierUrn = createIdentifierUrn(digitalObject);

    // mods:originInfo
    OriginInfo originInfo = createOriginInfo(digitalObject);

    Mods mods =
        Mods.builder()
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
