package de.digitalcollections.cudami.external.repository;

import de.digitalcollections.cudami.client.CudamiClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiCollectionsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiDigitalObjectsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiItemsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiManifestationsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiWorksClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.entity.work.Work;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CudamiRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(CudamiRepository.class);
  private final CudamiClient cudamiClient;

  public CudamiRepository(CudamiClient cudamiClient) {
    this.cudamiClient = cudamiClient;
  }

  public DigitalObject getDigitalObject(DigitalObject digitalObjectExample) {
    // get a fully filled WEMI-DigitalObject
    try {
      // DigitalObject
      CudamiDigitalObjectsClient cudamiDigitalObjectsClient = cudamiClient.forDigitalObjects();
      DigitalObject digitalObject =
          cudamiDigitalObjectsClient.getByUuid(digitalObjectExample.getUuid());

      if (digitalObject.getItem() != null) {
        // Item
        CudamiItemsClient cudamiItemsClient = cudamiClient.forItems();
        Item item = cudamiItemsClient.getByUuid(digitalObject.getItem().getUuid());
        digitalObject.setItem(item);

        if (item.getManifestation() != null) {
          // Manifestation
          CudamiManifestationsClient cudamiManifestationsClient = cudamiClient.forManifestations();
          Manifestation manifestation =
              cudamiManifestationsClient.getByUuid(item.getManifestation().getUuid());
          item.setManifestation(manifestation);

          if (manifestation.getWork() != null) {
            // Work
            CudamiWorksClient cudamiWorksClient = cudamiClient.forWorks();
            Work work = cudamiWorksClient.getByUuid(manifestation.getWork().getUuid());
            manifestation.setWork(work);
          }
        }
      }

      return digitalObject;
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObject by UUID.", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  public List<ImageFileResource> getIiifFileResources(DigitalObject digitalObject) {
    CudamiDigitalObjectsClient cudamiDigitalObjectsClient = cudamiClient.forDigitalObjects();
    try {
      List<ImageFileResource> imageFileResources =
          cudamiDigitalObjectsClient.getIiifImageFileResources(digitalObject.getUuid());
      return imageFileResources;
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObject by UUID.", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  public PageResponse<DigitalObject> findDigitalObjects(PageRequest pageRequest) {
    CudamiDigitalObjectsClient cudamiDigitalObjectsClient = cudamiClient.forDigitalObjects();
    try {
      return cudamiDigitalObjectsClient.find(pageRequest);
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObjects by page request.", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  public PageResponse<DigitalObject> findDigitalObjectsOfCollection(
      UUID collectionUuid, PageRequest pageRequest) {
    CudamiCollectionsClient cudamiCollectionsClient = cudamiClient.forCollections();
    try {
      return cudamiCollectionsClient.findDigitalObjects(collectionUuid, pageRequest);
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObjects by page request.", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
