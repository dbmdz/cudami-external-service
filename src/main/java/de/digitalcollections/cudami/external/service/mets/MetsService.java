package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.repository.CudamiRepositoryManager;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.LOCTYPE;
import org.mycore.libmeta.mets.model.filesec.File;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.filesec.file.FLocat;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class MetsService {

  private CudamiRepositoryManager cudamiRepositoryManager;

  public MetsService(CudamiRepositoryManager cudamiRepositoryManager) {
    this.cudamiRepositoryManager = cudamiRepositoryManager;
  }

  /**
   * Conforming METS documents must contain administrative metadata (AMD).
   *
   * @param mets METS object AmdSec should be added to
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
  protected AmdSec createAmdSec(DigitalObject digitalObject) {
    AmdSec amdSec = AmdSec.builder().ID("AMD").build();

    MdSec rightsMD = createRightsMD(digitalObject);
    amdSec.getRightsMD().add(rightsMD);

    MdSec digiprovMD = createDigiprovMD(digitalObject);
    amdSec.getDigiprovMD().add(digiprovMD);

    return amdSec;
  }

  protected MdSec createDigiprovMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("DIGIPROV").build();
    return mdSec;
  }

  /**
   * The overall purpose of the content file section element "fileSec" is to provide an inventory of
   * and the location for the content files that comprise the digital object being described in the
   * METS document
   */
  protected FileSec createFileSec(DigitalObject digitalObject) {
    // get IIIF-FileResources for DigitalObject and build different sizes/urls on
    // our own
    List<ImageFileResource> fileResources =
        cudamiRepositoryManager.getIiifFileResources(digitalObject);

    /**
     * A sequence of file group elements <fileGrp> can be used to group the digital files comprising
     * the content of a METS object.
     *
     * <p>Innerhalb von mets:fileSec kann es beliebig viele mets:fileGrp geben, die sich jedoch alle
     * durch den Wert des USE-Attributs unterscheiden müssen.
     *
     * <p>Es muss mindestens eine mets:fileGrp mit dem Attribut USE=“DEFAULT“ geben. Das Attribut
     * USE gibt den Verwendungszweck der in der Dateigruppe enthaltenen Repräsentationen an. Im
     * Kontext des DFG-Viewers werden die folgenden Attributwerte ausgewertet:
     *
     * <ul>
     *   <li>DEFAULT: normale Präsentationsderivate,
     *   <li>DOWNLOAD: herunterladbare (PDF-)Derivate,
     *   <li>THUMBS: Vorschaubilder je Seite (max. 150x150 Pixel),
     *   <li>TEASER: Voransicht des Werks (max. 150x150 Pixel),
     *   <li>AUDIO: für digitale Tonaufnahmen,
     *   <li>FULLTEXT: Volltext- und Layoutinformationen.
     * </ul>
     */
    FileGrp fileGrpDefault = createFileGrpDefault(fileResources);

    FileGrp fileGrpDownload = FileGrp.builder().USE("DOWNLOAD").build();

    FileSec fileSec =
        FileSec.builder()
            .addFileGrp(fileGrpDefault)
            // .addFileGrp(fileGrpMax)
            // .addFileGrp(fileGrpMin)
            .addFileGrp(fileGrpDownload)
            .build();
    return fileSec;
  }

  /**
   * DEFAULT: normale Präsentationsderivate
   *
   * <p>mets:fileGrp/mets:file: The file element <file> provides access to the content files for the
   * digital object being described by the METS document.
   *
   * <p>Das Attribut ID dient der Verknüpfung innerhalb der METS-Datei und muss zwingend eindeutig
   * belegt werden. Das Attribut MIMETYPE muss den Medientyp der digitalen Repräsentation angeben.
   * Erlaubt sind alle webfähigen Formate nach RFC204614 sowie die folgenden Spezialwerte:
   *
   * <ul>
   *   <li>application/vnd.kitodo.iiif: bei Verwendung der IIIF Image API 2.0+15,
   *   <li>application/vnd.netfpx: bei Verwendung des Internet Image Protocol (IIP)16,
   *   <li>application/vnd.kitodo.zoomify: bei Verwendung des proprietären Zoomify-Standards.
   * </ul>
   *
   * The file location element <FLocat> provides a pointer to the location of a content file. Der
   * Verweis muss zwingend in Form einer URL erfolgen. Bei Verwendung eines unterstützten Image
   * Servers (vgl. 2.4.2.2) ist die Base-URL inkl. Image ID anzugeben, jedoch keine weiteren
   * Parameter zur Skalierung, Rotation, etc.
   *
   * <p>Für IIIF:
   *
   * <pre>
   * <mets:file ID="FILE_0000_DEFAULT" MIMETYPE="application/vnd.kitodo.iiif">
   *   <mets:FLocat xmlns:xlink="http://www.w3.org/1999/xlink" LOCTYPE="URL"
   * xlink:href=
   * "https://api.digitale-sammlungen.de/iiif/image/v2/bsb00107608_00001"
   * />
   * </mets:file>
   * </pre>
   */
  private FileGrp createFileGrpDefault(List<ImageFileResource> fileResources) {
    FileGrp fileGrpDefault = FileGrp.builder().USE("DEFAULT").build();
    for (ImageFileResource imageFileResource : fileResources) {
      File file = File.builder().MIMETYPE("application/vnd.kitodo.iiif").build();
      fileGrpDefault.getFile().add(file);

      FLocat fLocat =
          FLocat.builder()
              .LOCTYPE(LOCTYPE.URL)
              .xlinkHref(imageFileResource.getHttpBaseUrl().toString())
              .build();
      file.getFLocat().add(fLocat);
    }
    return fileGrpDefault;
  }

  /**
   * rightsMD (intellectual property rights metadata) - Access Rights Policy, Copyrights Metadata.
   *
   * @param amdSec AmdSec-section the RightsMD should be add to
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
  protected MdSec createRightsMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("RIGHTS").build();
    return mdSec;
  }

  protected StructLink createStructLink(DigitalObject digitalObject) {
    StructLink structLink = StructLink.builder().build();
    return structLink;
  }

  /**
   * Logische Struktur – mets:structMap
   *
   * <p>The structural map is the heart of a METS document, defining the hierarchical arrangement of
   * a primary source document which has been digitized.
   *
   * <p>verpflichtend: Jede METS-Datei muss mindestens ein logisches Strukturelement enthalten. Für
   * die logische Struktur muss das Attribut TYPE mit dem Wert LOGICAL verwendet werden
   */
  protected StructMap createStructMapLogical(DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("LOGICAL").build();

    // Strukturelement – mets:div
    /**
     * The METS standard represents a document structurally as a series of nested div elements, that
     * is, as a hierarchy (e.g., a book, which is composed of chapters, which are composed of
     * subchapters, which are composed of text)
     *
     * <p>Die logische Struktur kann aus beliebig vielen mets:div aufgebaut werden, die zudem
     * beliebig in einander verschachtelt werden können, um die Hierarchie abzubilden.
     * verpflichtend: Jede METS-Datei muss mindestens ein logisches Strukturelement enthalten.
     *
     * <p>Das Attribut ID dient der Verknüpfung innerhalb der METS-Datei und muss zwingend eindeutig
     * belegt werden. Im Attribut TYPE muss die Art des Strukturelements näher bezeichnet werden.
     * Dabei sind nur Werte aus der DFG-Viewer-
     */
    return structMap;
  }

  protected StructMap createStructMapPhysical(DigitalObject digitalObject) {
    StructMap structMap = StructMap.builder().TYPE("PHYSICAL").build();
    return structMap;
  }

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    // mets:amdSec
    AmdSec amdSec = createAmdSec(digitalObject);

    // mets:fileSec
    FileSec fileSec = createFileSec(digitalObject);

    // mets:structMap TYPE="LOGICAL"
    StructMap structMapLogical = createStructMapLogical(digitalObject);

    // mets:structMap TYPE="PHYSICAL"
    StructMap structMapPhysical = createStructMapPhysical(digitalObject);

    // mets:structLink
    StructLink structLink = createStructLink(digitalObject);

    Mets mets =
        Mets.builder()
            .addAmdSec(amdSec)
            .fileSec(fileSec)
            .addStructMap(structMapLogical)
            .addStructMap(structMapPhysical)
            .structLink(structLink)
            .build();
    return mets;
  }
}
