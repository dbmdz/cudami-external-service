package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.springframework.stereotype.Service;

@Service
public class MetadataServiceImpl implements MetadataService {

  public AmdSec createAmdSec(DigitalObject digitalObject) {
    AmdSec amdSec = AmdSec.builder().ID("AMD").build();

    MdSec rightsMD = createRightsMD(digitalObject);
    amdSec.getRightsMD().add(rightsMD);

    MdSec digiprovMD = createDigiprovMD(digitalObject);
    amdSec.getDigiprovMD().add(digiprovMD);

    return amdSec;
  }

  public MdSec createDigiprovMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("DIGIPROV").build();
    return mdSec;
  }

  /**
   * rightsMD (intellectual property rights metadata) - Access Rights Policy, Copyrights Metadata.
   *
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
  public MdSec createRightsMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("RIGHTS").build();
    return mdSec;
  }
}
