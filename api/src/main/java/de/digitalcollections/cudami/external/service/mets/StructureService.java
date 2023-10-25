package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.util.List;
import org.mycore.libmeta.mets.model.structlink.StructLink;
import org.mycore.libmeta.mets.model.structmap.StructMap;

public interface StructureService {

  /**
   * Create structured links for the given list of ImageFileResources
   *
   * @param fileResources list of ImageFileResources
   * @return StructLink object
   */
  public StructLink createStructLink(List<ImageFileResource> fileResources);

  /**
   * Create the logical structure map for a DigitalObject
   *
   * <p>The METS standard represents a document structurally as a series of nested div elements,
   * that is, as a hierarchy (e.g., a book, which is composed of chapters, which are composed of
   * subchapters, which are composed of text)
   *
   * <p>Die logische Struktur kann aus beliebig vielen mets:div aufgebaut werden, die zudem beliebig
   * in einander verschachtelt werden können, um die Hierarchie abzubilden. verpflichtend: Jede
   * METS-Datei muss mindestens ein logisches Strukturelement enthalten.
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
   * <p>Das Attribut ORDERLABEL kann einen Ordnungswert wie z.B. eine Bandzählung enthalten, die in
   * der Navigation des DFG-Viewers erscheinen soll.
   *
   * <p>Existiert zum Strukturelement eine deskriptive Metadatensektion (siehe Kapitel 2.5), so ist
   * deren ID im Attribut DMDID anzugeben.
   *
   * <p>Für das primäre Strukturelement der METS-Datei ist im Attribut ADMID die ID der für den
   * DFG-Viewer relevanten administrativen Metadatensektion (siehe Kapitel 2.6) anzugeben.
   *
   * <p>Das Attribut CONTENTIDS sollte die das Strukturelement identifizierenden PURL und/oder URN
   * mit Leerzeichen getrennt enthalten.
   *
   * @param digitalObject the DigitalObject
   * @return StructMap object
   */
  public StructMap createStructMapLogical(DigitalObject digitalObject);

  /**
   * Create the physical structure map for a list of FileResources
   *
   * @param fileResources the list of FileResources
   * @return StructMap object
   */
  public StructMap createStructMapPhysical(List<ImageFileResource> fileResources);
}
