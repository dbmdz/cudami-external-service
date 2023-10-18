package de.digitalcollections.cudami.external.controller.mets;

import de.digitalcollections.cudami.external.service.mets.DfgMetsModsService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import org.mycore.libmeta.mets.METSXMLProcessor;
import org.mycore.libmeta.mets.model.Mets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DfgMetsModsController {

  public static final String UUID_PATTERN =
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

  private final DfgMetsModsService dfgMetsModsService;

  @SuppressFBWarnings
  public DfgMetsModsController(DfgMetsModsService dfgMetsModsService) {
    this.dfgMetsModsService = dfgMetsModsService;
  }

  @GetMapping(
      value = {"/mets/mods/v1/digitalobjects/{uuid:" + UUID_PATTERN + "}/dfg"},
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getByUuid(@PathVariable UUID uuid) throws Exception {
    DigitalObject digitalObject = new DigitalObject();
    digitalObject.setUuid(uuid);

    Mets mets = dfgMetsModsService.getMetsForDigitalObject(digitalObject);
    if (mets == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    String xml = METSXMLProcessor.getInstance().marshalToString(mets);
    return new ResponseEntity<String>(xml, HttpStatus.OK);
  }
}
