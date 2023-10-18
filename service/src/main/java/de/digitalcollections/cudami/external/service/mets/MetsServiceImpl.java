package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.repository.ListRepository;
import de.digitalcollections.cudami.external.repository.RepositoryException;
import de.digitalcollections.cudami.external.repository.SingleObjectRepository;
import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.LOCTYPE;
import org.mycore.libmeta.mets.model.div.Fptr;
import org.mycore.libmeta.mets.model.filesec.File;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.filesec.file.FLocat;
import org.mycore.libmeta.mets.model.mdsec.AmdSec;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.structlink.SmLink;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.Div;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class MetsServiceImpl
    implements MetadataService, FileService, StructureService, MetsService {

  private ListRepository listRepository;
  private SingleObjectRepository singleObjectRepository;

  public MetsServiceImpl(
      ListRepository listRepository, SingleObjectRepository singleObjectRepository) {
    this.listRepository = listRepository;
    this.singleObjectRepository = singleObjectRepository;
  }

  public AmdSec createAmdSec(DigitalObject digitalObject) {
    AmdSec amdSec = AmdSec.builder().ID("AMD").build();

    MdSec rightsMD = createRightsMD(digitalObject);
    amdSec.getRightsMD().add(rightsMD);

    MdSec digiprovMD = createDigiprovMD(digitalObject);
    amdSec.getDigiprovMD().add(digiprovMD);

    return amdSec;
  }

  public MdSec createDigiprovMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("DIGIPROV").build();
    return mdSec;
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
  public FileGrp createFileGrpDefault(List<ImageFileResource> fileResources) {
    FileGrp fileGrpDefault = FileGrp.builder().USE("DEFAULT").build();
    int i = 0;
    for (ImageFileResource imageFileResource : fileResources) {
      File file =
          File.builder()
              .ID("FILE_" + StringUtils.leftPad("" + i, 4, "0") + "_DEFAULT")
              .MIMETYPE("application/vnd.kitodo.iiif")
              .build();
      fileGrpDefault.getFile().add(file);

      FLocat fLocat =
          FLocat.builder()
              .LOCTYPE(LOCTYPE.URL)
              .xlinkHref(imageFileResource.getHttpBaseUrl().toString())
              .build();
      file.getFLocat().add(fLocat);
      i++;
    }
    return fileGrpDefault;
  }

  public FileGrp createFileGrpThumbs(List<ImageFileResource> fileResources) {
    FileGrp fileGrpThumbs = FileGrp.builder().USE("THUMBS").build();
    int i = 0;
    for (ImageFileResource imageFileResource : fileResources) {
      File file =
          File.builder()
              .ID("FILE_" + StringUtils.leftPad("" + i, 4, "0") + "_THUMBS")
              .MIMETYPE("image/jpeg")
              .build();
      fileGrpThumbs.getFile().add(file);

      FLocat fLocat =
          FLocat.builder()
              .LOCTYPE(LOCTYPE.URL)
              .xlinkHref(
                  imageFileResource.getHttpBaseUrl().toString() + "/full/!150,150/0/default.jpg")
              .build();
      file.getFLocat().add(fLocat);
      i++;
    }
    return fileGrpThumbs;
  }

  /**
   * The overall purpose of the content file section element "fileSec" is to provide an inventory of
   * and the location for the content files that comprise the digital object being described in the
   * METS document
   */
  public FileSec createFileSec(List<ImageFileResource> fileResources) {
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
    FileGrp fileGrpThumbs = createFileGrpThumbs(fileResources);

    //    FileGrp fileGrpDownload = FileGrp.builder().USE("DOWNLOAD").build();

    FileSec fileSec =
        FileSec.builder()
            .addFileGrp(fileGrpDefault)
            .addFileGrp(fileGrpThumbs)
            //            .addFileGrp(fileGrpDownload)
            .build();
    return fileSec;
  }

  /**
   * rightsMD (intellectual property rights metadata) - Access Rights Policy, Copyrights Metadata.
   *
   * @param digitalObject data object containing relevant data for filling
   * @return
   */
  public MdSec createRightsMD(DigitalObject digitalObject) {
    MdSec mdSec = MdSec.builder().ID("RIGHTS").build();
    return mdSec;
  }

  public StructLink createStructLink(List<ImageFileResource> fileResources) {
    StructLink structLink = StructLink.builder().build();

    int i = 1;
    for (ImageFileResource imageFileResource : fileResources) {
      String number = StringUtils.leftPad("" + i, 4, "0");
      SmLink smLink = SmLink.builder().xlinkTo("PHYS_" + number).xlinkFrom("LOG_0000").build();
      structLink.getSmLinkOrSmLinkGrp().add(smLink);
      i++;
    }

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
  public StructMap createStructMapLogical(DigitalObject digitalObject) {
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
     * belegt werden.
     *
     * <p>Im Attribut TYPE muss die Art des Strukturelements näher bezeichnet werden. Dabei sind nur
     * Werte aus der DFG-Viewer-Strukturdatenliste erlaubt.
     *
     * <p>Das Attribut LABEL kann eine Bezeichnung enthalten, unter der das Strukturelement in der
     * Navigation des DFG-Viewers erscheinen soll. Wird kein LABEL angegeben, erscheint dort der
     * Strukturtyp.
     *
     * <p>Das Attribut ORDERLABEL kann einen Ordnungswert wie z.B. eine Bandzählung enthalten, die
     * in der Navigation des DFG-Viewers erscheinen soll.
     *
     * <p>Existiert zum Strukturelement eine deskriptive Metadatensektion (siehe Kapitel 2.5), so
     * ist deren ID im Attribut DMDID anzugeben.
     *
     * <p>Für das primäre Strukturelement der METS-Datei ist im Attribut ADMID die ID der für den
     * DFG-Viewer relevanten administrativen Metadatensektion (siehe Kapitel 2.6) anzugeben.
     *
     * <p>Das Attribut CONTENTIDS sollte die das Strukturelement identifizierenden PURL und/oder URN
     * mit Leerzeichen getrennt enthalten.
     */
    Div.Builder divBuilder = Div.builder();
    divBuilder.ID("LOG_0000");

    String label = digitalObject.getLabel().getText();
    divBuilder.LABEL(label);

    if (digitalObject.getItem() != null && digitalObject.getItem().getManifestation() != null) {
      String type = digitalObject.getItem().getManifestation().getManifestationType();
      divBuilder.TYPE(type);
    }

    Div divTop = divBuilder.build();
    structMap.setDiv(divTop);

    return structMap;
  }

  public StructMap createStructMapPhysical(List<ImageFileResource> fileResources) {
    StructMap structMap = StructMap.builder().TYPE("PHYSICAL").build();
    Div div = Div.builder().ID("PHYS_0000").TYPE("physSequence").build();
    structMap.setDiv(div);

    int i = 1;
    for (ImageFileResource imageFileResource : fileResources) {
      String number = StringUtils.leftPad("" + i, 4, "0");
      Div subDiv =
          Div.builder().ID("PHYS_" + number).ORDER(i).ORDERLABEL(number).TYPE("page").build();
      div.getDiv().add(subDiv);

      Fptr fptrDefault =
          Fptr.builder()
              .FILEID("FILE_" + StringUtils.leftPad("" + (i - 1), 4, "0") + "_DEFAULT")
              .build();
      subDiv.getFptr().add(fptrDefault);

      Fptr fptrThumbs =
          Fptr.builder()
              .FILEID("FILE_" + StringUtils.leftPad("" + (i - 1), 4, "0") + "_THUMBS")
              .build();
      subDiv.getFptr().add(fptrThumbs);

      i++;
    }
    return structMap;
  }

  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws ServiceException {
    // mets:amdSec
    AmdSec amdSec = createAmdSec(digitalObject);

    // get (IIIF-)ImageFileResources for DigitalObject and build different sizes/urls on
    // our own
    try {
      List<ImageFileResource> fileResources = listRepository.getIiifFileResources(digitalObject);

      // mets:fileSec
      FileSec fileSec = createFileSec(fileResources);

      // mets:structMap TYPE="LOGICAL"
      StructMap structMapLogical = createStructMapLogical(digitalObject);

      // mets:structMap TYPE="PHYSICAL"
      StructMap structMapPhysical = createStructMapPhysical(fileResources);

      // mets:structLink
      StructLink structLink = createStructLink(fileResources);

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
}
