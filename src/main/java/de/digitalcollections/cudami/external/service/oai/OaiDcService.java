package de.digitalcollections.cudami.external.service.oai;

import de.digitalcollections.cudami.external.service.dublincore.DCService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.oaidc.model.OaiDc;
import org.springframework.stereotype.Service;

@Service
public class OaiDcService {
  private DCService dublinCoreService;

  public OaiDcService(DCService dublinCoreService) {
    this.dublinCoreService = dublinCoreService;
  }

  public OaiDc getOaiDcForDigitalObject(DigitalObject digitalObject) {
    OaiDc oaiDc =
        OaiDc.builder()
            .addAll(dublinCoreService.createDCTitles(digitalObject))
            .addAll(dublinCoreService.createDCDates(digitalObject))
            .addAll(dublinCoreService.createDCIdentifiers(digitalObject))
            .addAll(dublinCoreService.createDCLanguages(digitalObject))
            .build();
    // TODO add all fields and care about language attribute, too...
    return oaiDc;
  }
}
