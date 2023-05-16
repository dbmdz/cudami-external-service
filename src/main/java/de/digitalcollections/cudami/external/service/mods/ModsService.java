package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import org.mycore.libmeta.mods.model.Mods;
import org.mycore.libmeta.mods.model._toplevel.Location;
import org.mycore.libmeta.mods.model.location.ShelfLocator;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsService {
  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mods mods = new Mods();

    // mods:location/mods:shelfLocator
    Item item = digitalObject.getItem();
    Identifier shelfNo = item.getIdentifierByNamespace("shelfno");
    if (shelfNo != null) {
      ShelfLocator shelfLocator =
          ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
      Location location = Location.builderForLocation().addShelfLocator(shelfLocator).build();
      mods.getContent().add(location);
    }

    // mods:relatedItem/mods:titleInfo/mods:title

    return mods;
  }
}
