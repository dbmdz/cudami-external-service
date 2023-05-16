package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class MetsService {

  private void addAmdSec(Mets mets, DigitalObject digitalObject) {
    AmdSec amdSec = AmdSec.builder().ID("AMD").addRightsMD(null).build();
    mets.getAmdSec().add(amdSec);
  }

  private void addFileSec(Mets mets, DigitalObject digitalObject) {
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
    mets.setFileSec(fileSec);
  }

  private void addStructLink(Mets mets, DigitalObject digitalObject) {
    StructLink structLink = StructLink.builder().build();
    mets.setStructLink(structLink);
  }

  private void addStructMapLogical(Mets mets, DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("LOGICAL").build();
    mets.getStructMap().add(structMap);
  }

  private void addStructMapPhysical(Mets mets, DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("PHYSICAL").build();
    mets.getStructMap().add(structMap);
  }

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mets mets = new Mets();

    // mets:amdSec
    addAmdSec(mets, digitalObject);

    // mets:fileSec
    addFileSec(mets, digitalObject);

    // mets:structMap TYPE="LOGICAL"
    addStructMapLogical(mets, digitalObject);

    // mets:structMap TYPE="PHYSICAL"
    addStructMapPhysical(mets, digitalObject);

    // mets:structLink
    addStructLink(mets, digitalObject);

    return mets;
  }
}
