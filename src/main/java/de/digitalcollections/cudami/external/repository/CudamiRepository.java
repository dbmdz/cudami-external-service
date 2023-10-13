package de.digitalcollections.cudami.external.repository;

import de.digitalcollections.cudami.client.CudamiClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiCollectionsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiDigitalObjectsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiItemsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiManifestationsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiWorksClient;
import de.digitalcollections.cudami.external.monitoring.ProcessingMetrics;
import de.digitalcollections.cudami.external.monitoring.Watch;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.Collection;
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
  private final ProcessingMetrics metrics;

  public CudamiRepository(CudamiClient cudamiClient, ProcessingMetrics metrics) {
    this.cudamiClient = cudamiClient;
    this.metrics = metrics;
  }

  public PageResponse<Collection> findCollections(PageRequest pageRequest) {
    CudamiCollectionsClient cudamiCollectionsClient = cudamiClient.forCollections();
    try {
      return cudamiCollectionsClient.find(pageRequest);
    } catch (TechnicalException e) {
      LOGGER.error("can not get Collections by page request.", e);
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

  public Collection getCollection(String uuid) {
    CudamiCollectionsClient cudamiCollectionsClient = cudamiClient.forCollections();
    try {
      return cudamiCollectionsClient.getByUuid(UUID.fromString(uuid));
    } catch (TechnicalException e) {
      LOGGER.error("can not get Collection by uuid.", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  public DigitalObject getDigitalObject(DigitalObject digitalObjectExample) {
    Watch digitalObjectWatch =
        this.metrics.startMeasure(ProcessingMetrics.MetadataOperation.GET_DIGITALOBJECT);
    Watch itemWatch = this.metrics.startMeasure(ProcessingMetrics.MetadataOperation.GET_ITEM);
    Watch manifestationWatch =
        this.metrics.startMeasure(ProcessingMetrics.MetadataOperation.GET_MANIFESTATION);
    Watch workWatch = this.metrics.startMeasure(ProcessingMetrics.MetadataOperation.GET_WORK);

    Watch fullDigitalObjectWatch =
        this.metrics.startMeasure(ProcessingMetrics.MetadataOperation.GET_FULL_DIGITALOBJECT);
    // get a fully filled WEMI-DigitalObject
    try {
      // DigitalObject
      CudamiDigitalObjectsClient cudamiDigitalObjectsClient = cudamiClient.forDigitalObjects();
      digitalObjectWatch.reset();
      DigitalObject digitalObject =
          cudamiDigitalObjectsClient.getByUuid(digitalObjectExample.getUuid());
      digitalObjectWatch.stop();

      // Parent digitalObject - fill recursively
      if (digitalObject.getParent() != null) {
        digitalObject.setParent(getDigitalObject(digitalObject.getParent()));
      }

      if (digitalObject.getItem() != null) {
        // Item
        CudamiItemsClient cudamiItemsClient = cudamiClient.forItems();
        itemWatch.reset();
        Item item = cudamiItemsClient.getByUuid(digitalObject.getItem().getUuid());
        itemWatch.stop();
        digitalObject.setItem(item);

        // Parent item
        if (item.getPartOfItem() != null) {
          itemWatch.reset();
          Item partOfItemm = cudamiItemsClient.getByUuid(item.getPartOfItem().getUuid());
          itemWatch.stop();
          item.setPartOfItem(partOfItemm);
        }

        if (item.getManifestation() != null) {
          // Manifestation
          CudamiManifestationsClient cudamiManifestationsClient = cudamiClient.forManifestations();
          manifestationWatch.reset();
          Manifestation manifestation =
              cudamiManifestationsClient.getByUuid(item.getManifestation().getUuid());
          manifestationWatch.stop();
          item.setManifestation(manifestation);

          if (manifestation.getWork() != null) {
            // Work
            CudamiWorksClient cudamiWorksClient = cudamiClient.forWorks();
            workWatch.reset();
            Work work = cudamiWorksClient.getByUuid(manifestation.getWork().getUuid());
            workWatch.stop();
            manifestation.setWork(work);
          }
        }
      }

      fullDigitalObjectWatch.stop();

      return digitalObject;
    } catch (TechnicalException e) {
      LOGGER.error("can not get DigitalObject by UUID.", e);
      throw new RuntimeException(e.getMessage());
    } finally {
      digitalObjectWatch.stop();
      itemWatch.stop();
      manifestationWatch.stop();
      workWatch.stop();
      fullDigitalObjectWatch.stop();
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
}
