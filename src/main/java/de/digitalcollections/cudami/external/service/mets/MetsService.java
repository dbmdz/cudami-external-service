package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class MetsService {

  /**
   * Conforming METS documents must contain administrative metadata (AMD).
   *
   * @param mets METS object AmdSec should be added to
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
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

  public FileSec createFileSec(DigitalObject digitalObject) {
    FileGrp fileGrpDefault = FileGrp.builder().USE("DEFAULT").build();
    FileGrp fileGrpMax = FileGrp.builder().USE("MAX").build();
    FileGrp fileGrpMin = FileGrp.builder().USE("MIN").build();
    FileGrp fileGrpDownload = FileGrp.builder().USE("DOWNLOAD").build();
    FileSec fileSec =
        FileSec.builder()
            .addFileGrp(fileGrpDefault)
            .addFileGrp(fileGrpMax)
            .addFileGrp(fileGrpMin)
            .addFileGrp(fileGrpDownload)
            .build();
    return fileSec;
  }

  /**
   * rightsMD (intellectual property rights metadata) - Access Rights Policy, Copyrights Metadata.
   *
   * @param amdSec AmdSec-section the RightsMD should be add to
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
  public MdSec createRightsMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("RIGHTS").build();
    return mdSec;
  }

  public StructLink createStructLink(DigitalObject digitalObject) {
    StructLink structLink = StructLink.builder().build();
    return structLink;
  }

  public StructMap createStructMapLogical(DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("LOGICAL").build();
    return structMap;
  }

  public StructMap createStructMapPhysical(DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("PHYSICAL").build();
    return structMap;
  }

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    // mets:amdSec
    AmdSec amdSec = createAmdSec(digitalObject);

    // mets:fileSec
    FileSec fileSec = createFileSec(digitalObject);

    // mets:structMap TYPE="LOGICAL"
    StructMap structMapLogical = createStructMapLogical(digitalObject);

    // mets:structMap TYPE="PHYSICAL"
    StructMap structMapPhysical = createStructMapPhysical(digitalObject);

    // mets:structLink
    StructLink structLink = createStructLink(digitalObject);

    Mets mets =
        Mets.builder()
            .addAmdSec(amdSec)
            .fileSec(fileSec)
            .addStructMap(structMapLogical)
            .addStructMap(structMapPhysical)
            .structLink(structLink)
            .build();
    return mets;
  }
}
