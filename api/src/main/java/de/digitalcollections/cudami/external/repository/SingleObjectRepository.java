package de.digitalcollections.cudami.external.repository;

import de.digitalcollections.model.identifiable.entity.Collection;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;

/** Repository to retrieve single model objects */
public interface SingleObjectRepository {

  /**
   * Retrieves a collection by its UUID
   *
   * @param uuid the UUID of the collection
   * @return the collection or null, if no collection with the given UUID exists
   * @throws RepositoryException in case of an error
   */
  public Collection getCollection(String uuid) throws RepositoryException;

  /**
   * Retrieves a DigitalObject by an example DigitalObject
   *
   * @param digitalObjectExample example DigitalObject
   * @return the DigitalObject or null, if no DigitalObject for the given example exists
   * @throws RepositoryException in case of an error
   */
  public DigitalObject getDigitalObject(DigitalObject digitalObjectExample)
      throws RepositoryException;

  /**
   * Retrieves a manifestation by example Manifestation (either uuid or identifier)
   *
   * @param manifestation example Manifestation
   * @return the manifestation, or null
   * @throws RepositoryException in case of an error
   */
  public Manifestation getManifestation(Manifestation manifestation) throws RepositoryException;
}