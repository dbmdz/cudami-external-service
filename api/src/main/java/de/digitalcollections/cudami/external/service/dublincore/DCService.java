package de.digitalcollections.cudami.external.service.dublincore;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import java.util.List;
import org.mycore.libmeta.dcsimple.model.DCElement;

/** Service for Dublin Core handling */
public interface DCService {

  /**
   * Creates a list of Dublin Core dates for a DigitalObject {@see
   * https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/date/}
   *
   * @param digitalObject the DigitalObject
   * @return List of DCElements
   * @throws ServiceException in case of an error
   */
  public List<DCElement> createDCDates(DigitalObject digitalObject) throws ServiceException;

  /**
   * Creates a list of Dublin Core identifiers for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return List of DCElements
   * @throws ServiceException in case of an error
   */
  public List<DCElement> createDCIdentifiers(DigitalObject digitalObject) throws ServiceException;

  /**
   * Creates a list of Dublin Core languages for a DigitalObject {@see
   * https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/language/}
   *
   * @param digitalObject the DigitalObject
   * @return List of DCElements
   * @throws ServiceException in case of an error
   */
  public List<DCElement> createDCLanguages(DigitalObject digitalObject) throws ServiceException;

  /**
   * Creates a list of Dublin Core titles for a DigitalObject
   *
   * @param digitalObject the DigitalObject
   * @return List of DCElements
   * @throws ServiceException in case of an error
   */
  public List<DCElement> createDCTitles(DigitalObject digitalObject) throws ServiceException;
}
