package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;

import java.util.List;

public interface StructureService {

  /**
   * Create structured links for the given list of ImageFileResources
   * @param fileResources list of ImageFileResources
   * @return StructLink object
   */
  public StructLink createStructLink(List<ImageFileResource> fileResources);

  /**
   * Create the logical structure map for a DigitalObject
   * @param digitalObject the DigitalObject
   * @return StructMap object
   */
  public StructMap createStructMapLogical(DigitalObject digitalObject);

  /**
   * Create the physical structure map for a list of FileResources
   * @param fileResources the list of FileResources
   * @return StructMap object
   */
  public StructMap createStructMapPhysical(List<ImageFileResource> fileResources);
}
