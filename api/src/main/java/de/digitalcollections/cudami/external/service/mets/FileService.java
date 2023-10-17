package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;

public interface FileService {

  /**
   * Creates the File group for the default images out of the given list of ImageFileResources
   *
   * @param fileResources list of ImageFileResources
   * @return FileGrp object
   */
  public FileGrp createFileGrpDefault(List<ImageFileResource> fileResources);

  /**
   * Creates the File group for the thumbnail images out of the given list of ImageFileResources
   *
   * @param fileResources list of ImageFileResource
   * @return FileGrp object
   */
  public FileGrp createFileGrpThumbs(List<ImageFileResource> fileResources);

  /**
   * Create the content file section out of the given list of ImageFileResources
   *
   * @param fileResources the list of ImageFileResources
   * @return FileSec object
   */
  public FileSec createFileSec(List<ImageFileResource> fileResources);
}
