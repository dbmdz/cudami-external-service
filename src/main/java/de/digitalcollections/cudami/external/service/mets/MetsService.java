package de.digitalcollections.cudami.external.service.mets;

import org.mycore.libmeta.mets.model.Mets;
import org.springframework.stereotype.Service;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;

/**
 * Service for creation of METS metadata by given (fully filled) DigitalObject.
 */
@Service
public class MetsService {
  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mets mets = new Mets();
    return mets;
  }
}
