package de.digitalcollections.cudami.external.util;

import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.text.Title;
import de.digitalcollections.model.text.TitleType;

public class TitleUtil {
  public static String filterTitle(Manifestation manifestation, String mainType, String subType) {
    if (manifestation == null) {
      return "";
    }

    TitleType titleType = new TitleType(mainType, subType);
    Title title =
        manifestation.getTitles().stream()
            .filter(t -> t.getTitleType().equals(titleType))
            .findFirst()
            .orElse(null);

    if (title == null) {
      return null;
    }

    return title.getText().getText();
  }
}
