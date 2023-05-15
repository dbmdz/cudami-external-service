package de.digitalcollections.cudami.external.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import de.digitalcollections.cudami.client.CudamiClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiDigitalObjectsClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;

@Repository
public class CudamiRepositoryManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(CudamiRepositoryManager.class);
  private final CudamiClient cudamiClient;

  public CudamiRepositoryManager(CudamiClient cudamiClient) {
    this.cudamiClient = cudamiClient;
  }

  public DigitalObject getDigitalObject(DigitalObject digitalObject) {
    CudamiDigitalObjectsClient client = cudamiClient.forDigitalObjects();
    try {
      return client.getByUuid(digitalObject.getUuid());
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObject by UUID.", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
