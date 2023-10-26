package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.repository.ListRepository;
import de.digitalcollections.cudami.external.repository.RepositoryException;
import de.digitalcollections.cudami.external.repository.SingleObjectRepository;
import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) WMID objects. */
@Service
public class MetsServiceImpl implements MetsService {

  private FileService fileService;
  private ListRepository listRepository;
  private MetadataService metadataService;
  private SingleObjectRepository singleObjectRepository;
  private StructureService structureService;

  public MetsServiceImpl(
      ListRepository listRepository,
      SingleObjectRepository singleObjectRepository,
      MetadataService metadataService,
      FileService fileService,
      StructureService structureService) {
    this.fileService = fileService;
    this.listRepository = listRepository;
    this.metadataService = metadataService;
    this.singleObjectRepository = singleObjectRepository;
    this.structureService = structureService;
  }

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws ServiceException {
    // mets:amdSec
    AmdSec amdSec = metadataService.createAmdSec(digitalObject);

    // get (IIIF-)ImageFileResources for DigitalObject and build different sizes/urls on
    // our own
    try {
      List<ImageFileResource> fileResources = listRepository.getIiifFileResources(digitalObject);

      // mets:fileSec
      FileSec fileSec = fileService.createFileSec(fileResources);

      // mets:structMap TYPE="LOGICAL"
      StructMap structMapLogical = structureService.createStructMapLogical(digitalObject);

      // mets:structMap TYPE="PHYSICAL"
      StructMap structMapPhysical = structureService.createStructMapPhysical(fileResources);

      // mets:structLink
      StructLink structLink = structureService.createStructLink(fileResources);

      Mets mets =
          Mets.builder()
              .addAmdSec(amdSec)
              .fileSec(fileSec)
              .addStructMap(structMapLogical)
              .addStructMap(structMapPhysical)
              .structLink(structLink)
              .build();
      return mets;
    } catch (RepositoryException e) {
      throw new ServiceException(
          "Cannot get Mets for a DigitalObject=" + digitalObject + ": " + e, e);
    }
  }

  @Override
  public Mets getFullCalendarMetsForManifestation(Manifestation manifestation)
      throws ServiceException {
    if (manifestation.getLastModified() == null) {
      try {
        manifestation = singleObjectRepository.getManifestation(manifestation);
      } catch (RepositoryException e) {
        throw new ServiceException(
            "Cannot get Mets for full calendar for manifestation=" + manifestation + ": " + e, e);
      }
    }

    // mets:amdSec
    AmdSec amdSec = metadataService.createAmdSec(manifestation);

    Mets mets = Mets.builder().addAmdSec(amdSec).build();
    return mets;
  }
}
