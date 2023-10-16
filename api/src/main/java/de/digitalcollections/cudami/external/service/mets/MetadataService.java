package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.mdsec.MdSec;

public interface MetadataService {

  /**
   * Conforming METS documents must contain administrative metadata (AMD).
   *
   * @param digitalObject data object containing relevant data for filling
   * @return the AmdSec object
   */
  public AmdSec createAmdSec(DigitalObject digitalObject);

  /**
   * Create the Digiprov metadata section
   * @param digitalObject the DigitalObject
   * @return MdSec object
   */
  public MdSec createDigiprovMD(DigitalObject digitalObject);

  /**
   * Create the rights metadata section
   * @param digitalObject the DigitalObject
   * @return MdSec object
   */
  public MdSec createRightsMD(DigitalObject digitalObject);
}
