package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mycore.libmeta.mets.model.div.Fptr;
import org.mycore.libmeta.mets.model.structlink.SmLink;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.Div;
import org.mycore.libmeta.mets.model.structmap.StructMap;
import org.springframework.stereotype.Service;

@Service
public class StructureServiceImpl implements StructureService {

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

  @Override
  public StructMap createStructMapLogical(Manifestation manifestation) {
    StructMap structMap = StructMap.builder().TYPE("LOGICAL").build();

    Div.Builder divBuilder = Div.builder();
    divBuilder.ID("LOG_0000").ADMID("AMD").DMDID("DMDLOG_0000");

    String label = manifestation.getLabel().getText();
    divBuilder.LABEL(label);

    divBuilder.TYPE(manifestation.getManifestationType());

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
}
