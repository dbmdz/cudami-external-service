package de.digitalcollections.cudami.external.service.oai;

import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.cudami.external.service.dublincore.DCService;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.mycore.libmeta.oaidc.model.OaiDc;
import org.springframework.stereotype.Service;

@Service
public class OaiDcServiceImpl implements OaiDcService {
  private DCService dcService;

  public OaiDcServiceImpl(DCService dcService) {
    this.dcService = dcService;
  }

  public OaiDc getOaiDcForDigitalObject(DigitalObject digitalObject) throws ServiceException {
    OaiDc oaiDc =
        OaiDc.builder()
            .addAll(dcService.createDCTitles(digitalObject))
            .addAll(dcService.createDCDates(digitalObject))
            .addAll(dcService.createDCIdentifiers(digitalObject))
            .addAll(dcService.createDCLanguages(digitalObject))
            .build();
    // TODO add all fields and care about language attribute, too...
    return oaiDc;
  }
}
