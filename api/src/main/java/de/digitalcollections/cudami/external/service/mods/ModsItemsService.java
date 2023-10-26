package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import org.mycore.libmeta.mods.model._toplevel.*;

/** Service to create the individual Mods parts */
public interface ModsItemsService {

  /**
   * Create the purl indentifier for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the purl identifier
   */
  Identifier createIdentifierPurl(DigitalObject digitalObject);

  /**
   * Create the URN identifier for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the URN identifier
   */
  Identifier createIdentifierUrn(DigitalObject digitalObject);

  /**
   * Create the location (<code>mods:shelfLocator</code>) for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the location
   */
  Location createLocation(DigitalObject digitalObject);

  /**
   * Create the OriginInfo for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the origin info
   */
  OriginInfo createOriginInfo(DigitalObject digitalObject);

  /**
   * Create the physical description for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the physical description
   */
  PhysicalDescription createPhysicalDescription(DigitalObject digitalObject);

  /**
   * Create the physical description for a Manifestation
   *
   * @param manifestation the Manifestation
   * @return the physical description
   */
  PhysicalDescription createPhysicalDescription(Manifestation manifestation);

  /**
   * Create the related item for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return the related item
   */
  RelatedItem createRelatedItem(DigitalObject digitalObject);
}
