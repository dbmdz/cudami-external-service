package de.digitalcollections.cudami.external.repository;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;

/** Retrieve an unfiltered list of model objects */
public interface ListRepository {

  /**
   * Retrieve a list of ImageFileResources for IIIF, belonging to a DigitalObject
   *
   * @param digitalObject the DigitalObject for retrieval
   * @return a list of ImageFileResources
   * @throws RepositoryException in case of an error
   */
  public List<ImageFileResource> getIiifFileResources(DigitalObject digitalObject)
      throws RepositoryException;
}
