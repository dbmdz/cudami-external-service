package de.digitalcollections.cudami.external.util;

import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;

public class ManifestationHelper {

  /**
   * Returns, if the manifestation belongs to a newspaper issue. This is the case, when the
   * following criteria are all met:
   *
   * <ul>
   *   <li>manifestation type is <code>ADO</code>
   *   <li>has got one (1) parent manifestation
   *   <li>parent manifestation type is either <code>NEWSPAPER</code> or <code>JOURNAL</code>
   *   <li>
   * </ul>
   *
   * @param manifestation
   * @return
   */
  public static boolean isNewspaperIssue(Manifestation manifestation) {
    if (manifestation == null) {
      return false;
    }

    if (!"ADO".equalsIgnoreCase(manifestation.getManifestationType())) {
      return false;
    }

    if (manifestation.getParents() == null || manifestation.getParents().size() != 1) {
      return false;
    }

    String parentManifestationType =
        manifestation.getParents().get(0).getSubject().getManifestationType();

    return ("NEWSPAPER".equalsIgnoreCase(parentManifestationType)
        || "JOURNAL".equalsIgnoreCase(parentManifestationType));
  }
}
