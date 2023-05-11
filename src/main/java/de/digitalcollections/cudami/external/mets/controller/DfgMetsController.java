package de.digitalcollections.cudami.external.mets.controller;

import java.util.UUID;

import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.MDTYPE;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.mdsec.MdWrap;
import org.mycore.libmeta.mets.model.mdsec.XMLData;
import org.mycore.libmeta.mods.MODSXMLProcessor;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

@RestController
public class DfgMetsController {

  public static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

  @GetMapping(value = {
      "/mets/v1/digitalobjects/{uuid:" + UUID_PATTERN + "}" }, produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getByUuid(@PathVariable UUID uuid) throws Exception {
    Mods mods = new Mods();
    Mets mets = new Mets();
    fillMets(mets, mods);
    
    
    String xml = METSXMLProcessor.getInstance().marshalToString(mets);
//    String xml = """
//         <?xml version="1.0" encoding="UTF-8"?>
//           <mets:mets xmlns:mets="http://www.loc.gov/METS/"
//                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//                      xmlns:mx="http://www.loc.gov/MARC21/slim"
//                      xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/mods.xsd http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd http://www.loc.gov/standards/premis/ http://www.loc.gov/standards/premis/v2/premis-v2-0.xsd http://www.loc.gov/standards/mix/ http://www.loc.gov/standards/mix/mix.xsd">
//        </mets:mets>
//         """;
    return new ResponseEntity<String>(xml, HttpStatus.OK);
  }

  private void fillMets(Mets mets, Mods mods) throws Exception {
    Element modsElement = MODSXMLProcessor.getInstance().marshalToDOM(mods).getDocumentElement();
    MdWrap mdWrap = MdWrap.builder().MDTYPE(MDTYPE.MODS).build();
    mdWrap.setXmlData(XMLData.builder().addNode(modsElement).build());
    MdSec mdSec = MdSec.builder().mdWrap(mdWrap).build();
    mets.getDmdSec().add(mdSec);
  }
}
