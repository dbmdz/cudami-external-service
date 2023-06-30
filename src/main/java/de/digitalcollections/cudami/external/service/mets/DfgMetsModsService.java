package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.config.DfgConfig;
import de.digitalcollections.cudami.external.repository.CudamiRepositoryManager;
import de.digitalcollections.cudami.external.service.mods.DfgModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.dfgviewer.DVRightsXMLProcessor;
import org.mycore.libmeta.dfgviewer.model.Rights;
import org.mycore.libmeta.dfgviewer.model.Rights.Builder;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.MDTYPE;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.mdsec.MdWrap;
import org.mycore.libmeta.mets.model.mdsec.XMLData;
import org.mycore.libmeta.mods.MODSXMLProcessor;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

/** Service for creation of DFG specific METS metadata by given (fully filled) DigitalObject. */
@Service
public class DfgMetsModsService extends MetsService {
  private final DfgConfig dfgConfig;
  private final DfgModsService dfgModsService;

  public DfgMetsModsService(
      DfgModsService dfgModsService,
      CudamiRepositoryManager cudamiRepositoryManager,
      DfgConfig dfgConfig) {
    super(cudamiRepositoryManager);
    this.dfgConfig = dfgConfig;
    this.dfgModsService = dfgModsService;
  }

  @Override
  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mets mets = super.getMetsForDigitalObject(digitalObject);

    // add MODS to mets:dmdSec
    Mods mods = dfgModsService.getModsForDigitalObject(digitalObject);
    Element modsElement = MODSXMLProcessor.getInstance().marshalToDOM(mods).getDocumentElement();
    MdWrap mdWrap = MdWrap.builder().MDTYPE(MDTYPE.MODS).build();
    mdWrap.setXmlData(XMLData.builder().addNode(modsElement).build());
    MdSec mdSec = MdSec.builder().mdWrap(mdWrap).build();
    mets.getDmdSec().add(mdSec);

    // add DVRIGHTS to <mets:amdSec ID="AMD">/<mets:rightsMD ID="RIGHTS">/.../dv:rights
    Rights rights = getDfgRightsForDigitalObject(digitalObject);
    Element rightsElement =
        DVRightsXMLProcessor.getInstance().marshalToDOM(rights).getDocumentElement();
    MdWrap mdWrapDVRights =
        MdWrap.builder().MDTYPE(MDTYPE.OTHER).MIMETYPE("text/xml").OTHERMDTYPE("DVRIGHTS").build();
    mdWrapDVRights.setXmlData(XMLData.builder().addNode(rightsElement).build());
    MdSec rightsMD = mets.getAmdSec().get(0).getRightsMD().get(0); // has been created before
    rightsMD.setMdWrap(mdWrapDVRights);

    // add DVLINKS to <mets:amdSec ID="AMD">/<mets:digiprovMD ID="DIGIPROV">
    //    MdSec digiprovMD = mets.getAmdSec().get(0).getDigiprovMD().get(0); // has been created
    // before

    return mets;
  }

  private Rights getDfgRightsForDigitalObject(DigitalObject digitalObject) {
    Builder rightsBuilder =
        Rights.builder()
            .owner(dfgConfig.getRights().getOwner())
            .ownerContact(dfgConfig.getRights().getOwnerContact())
            .ownerLogo(dfgConfig.getRights().getOwnerLogo().toString())
            .ownerSiteURL(dfgConfig.getRights().getOwnerSiteUrl().toString());
    return rightsBuilder.build();
  }
}
