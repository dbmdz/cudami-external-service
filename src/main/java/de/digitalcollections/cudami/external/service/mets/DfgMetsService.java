package de.digitalcollections.cudami.external.service.mets;

import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.MDTYPE;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.mdsec.MdWrap;
import org.mycore.libmeta.mets.model.mdsec.XMLData;
import org.mycore.libmeta.mods.MODSXMLProcessor;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import de.digitalcollections.cudami.external.service.mods.DfgModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;

/**
 * Service for creation of DFG specific METS metadata by given (fully filled)
 * DigitalObject.
 */
@Service
public class DfgMetsService extends MetsService {
  private DfgModsService dfgModsService;

  public DfgMetsService(DfgModsService dfgModsService) {
    this.dfgModsService = dfgModsService;
  }

  @Override
  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mets mets = super.getMetsForDigitalObject(digitalObject);
    Mods mods = dfgModsService.getModsForDigitalObject(digitalObject);

    Element modsElement = MODSXMLProcessor.getInstance().marshalToDOM(mods).getDocumentElement();
    MdWrap mdWrap = MdWrap.builder().MDTYPE(MDTYPE.MODS).build();
    mdWrap.setXmlData(XMLData.builder().addNode(modsElement).build());
    MdSec mdSec = MdSec.builder().mdWrap(mdWrap).build();
    mets.getDmdSec().add(mdSec);
    return mets;
  }
}
