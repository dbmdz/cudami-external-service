package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mycore.libmeta.mets.model._enums.LOCTYPE;
import org.mycore.libmeta.mets.model.filesec.File;
import org.mycore.libmeta.mets.model.filesec.FileGrp;
import org.mycore.libmeta.mets.model.filesec.FileSec;
import org.mycore.libmeta.mets.model.filesec.file.FLocat;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

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
}
