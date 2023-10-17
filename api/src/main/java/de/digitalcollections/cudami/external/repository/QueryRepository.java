package de.digitalcollections.cudami.external.repository;

import de.digitalcollections.model.identifiable.entity.Collection;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import java.util.UUID;

/** Repository for querying (filtering) model object retrieval */
public interface QueryRepository {

  /**
   * Retrieve a list of collections
   *
   * @param pageRequest the pageRequest
   * @return the PageResponse
   * @throws RepositoryException in case of an error
   */
  public PageResponse<Collection> findCollections(PageRequest pageRequest)
      throws RepositoryException;

  /**
   * Retrieve a list of DigitalObjects
   *
   * @param pageRequest the pageRequest
   * @return the PageResponse
   * @throws RepositoryException in case of an error
   */
  public PageResponse<DigitalObject> findDigitalObjects(PageRequest pageRequest)
      throws RepositoryException;

  /**
   * Retrieves a list of DigitalObjects of a collection
   *
   * @param collectionUuid the UUID of the collection
   * @param pageRequest the pageRequest
   * @return the pageResponse
   * @throws RepositoryException in case of an error
   */
  public PageResponse<DigitalObject> findDigitalObjectsOfCollection(
      UUID collectionUuid, PageRequest pageRequest) throws RepositoryException;
}
