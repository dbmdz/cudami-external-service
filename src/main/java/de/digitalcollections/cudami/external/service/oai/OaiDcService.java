package de.digitalcollections.cudami.external.service.oai;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.dcsimple.model.DCTitle;
import org.mycore.libmeta.oaidc.model.OaiDc;
import org.springframework.stereotype.Service;

@Service
public class OaiDcService {
  public OaiDc getOaiDcForDigitalObject(DigitalObject digitalObject) {
    OaiDc oaiDc =
        OaiDc.builder()
            .add(DCTitle.builder().value(digitalObject.getLabel().getText()).build())
            .build();
    // TODO add all fields and care about language attribute, too...
    return oaiDc;
  }
}
