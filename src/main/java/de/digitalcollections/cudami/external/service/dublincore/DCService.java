package de.digitalcollections.cudami.external.service.dublincore;

import com.datazuul.language.iso639.ISO639Languages;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import jakarta.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.mycore.libmeta.dcsimple.model.DCDate;
import org.mycore.libmeta.dcsimple.model.DCIdentifier;
import org.mycore.libmeta.dcsimple.model.DCLanguage;
import org.mycore.libmeta.dcsimple.model.DCTitle;
import org.mycore.libmeta.dcsimple.model.ElementType;
import org.springframework.stereotype.Service;

@Service
public class DCService {

  // contributor

  // dates
  // see https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/date/
  // TODO: even ranges can be expressed with "/", e.g. 1919/1925
  public List<JAXBElement<ElementType>> createDCDates(DigitalObject digitalObject) {
    List<JAXBElement<ElementType>> result = new ArrayList<>();
    Manifestation manifestation = getManifestation(digitalObject);
    if (manifestation != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

      LocalDate publicationDate = manifestation.getPublicationInfo().getNavDateRange().getStart();
      result.add(DCDate.builder().value(publicationDate.format(formatter)).build());
    }
    return result;
  }

  // identifiers
  public List<JAXBElement<ElementType>> createDCIdentifiers(DigitalObject digitalObject) {
    List<JAXBElement<ElementType>> result = new ArrayList<>();
    Set<Identifier> identifiers = digitalObject.getIdentifiers();
    for (Identifier identifier : identifiers) {
      result.add(
          DCIdentifier.builder()
              .value(identifier.getNamespace() + ":" + identifier.getId())
              .build());
    }
    return result;
  }

  // languages
  // see https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/language/
  public List<JAXBElement<ElementType>> createDCLanguages(DigitalObject digitalObject) {
    List<JAXBElement<ElementType>> result = new ArrayList<>();

    Manifestation manifestation = getManifestation(digitalObject);
    if (manifestation != null) {
      Locale manifestationLanguage = manifestation.getLanguage();
      if (manifestationLanguage != null) {
        String language = ISO639Languages.getByLocale(manifestationLanguage).getPart2B();
        result.add(DCLanguage.builder().value(language).build());
      }

      // other languages
      LinkedHashSet<Locale> otherLanguages = manifestation.getOtherLanguages();
      if (otherLanguages != null && !otherLanguages.isEmpty()) {
        for (Locale otherLanguage : otherLanguages) {
          String language = ISO639Languages.getByLocale(otherLanguage).getPart2B();
          result.add(DCLanguage.builder().value(language).build());
        }
      }
    }
    return result;
  }

  // titles
  public List<JAXBElement<ElementType>> createDCTitles(DigitalObject digitalObject) {
    List<JAXBElement<ElementType>> result = new ArrayList<>();
    if (digitalObject.getLabel() != null) {
      result.add(DCTitle.builder().value(digitalObject.getLabel().getText()).build());
    }
    return result;
  }

  private Manifestation getManifestation(DigitalObject digitalObject) {
    Item item = digitalObject.getItem();
    if (item != null
        && item.getExemplifiesManifestation() != null
        && item.getExemplifiesManifestation()) {
      return item.getManifestation();
    }
    return null;
  }
}
