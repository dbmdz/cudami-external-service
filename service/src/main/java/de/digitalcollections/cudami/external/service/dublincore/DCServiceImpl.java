package de.digitalcollections.cudami.external.service.dublincore;

import com.datazuul.language.iso639.ISO639Languages;
import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.mycore.libmeta.dcsimple.model.*;
import org.springframework.stereotype.Service;

@Service
public class DCServiceImpl implements DCService {

  // contributor

  // dates
  // see https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/date/
  // TODO: even ranges can be expressed with "/", e.g. 1919/1925
  public List<DCElement> createDCDates(DigitalObject digitalObject) throws ServiceException {
    try {
      List<DCElement> result = new ArrayList<>();
      Manifestation manifestation = getManifestation(digitalObject);
      if (manifestation != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate publicationDate = manifestation.getPublicationInfo().getNavDateRange().getStart();
        result.add(DCDate.builder().value(publicationDate.format(formatter)).build());
      }
      return result;
    } catch (Exception e) {
      throw new ServiceException(
          "Cannot create DC Dates for DigitalObject with uuid="
              + digitalObject.getUuid()
              + ": "
              + e,
          e);
    }
  }

  // identifiers
  public List<DCElement> createDCIdentifiers(DigitalObject digitalObject) throws ServiceException {
    try {
      List<DCElement> result = new ArrayList<>();
      Set<Identifier> identifiers = digitalObject.getIdentifiers();
      for (Identifier identifier : identifiers) {
        result.add(
            DCIdentifier.builder()
                .value(identifier.getNamespace() + ":" + identifier.getId())
                .build());
      }
      return result;
    } catch (Exception e) {
      throw new ServiceException(
          "Cannot create DC Identifiers fpr DigitalObject with uuid="
              + digitalObject.getUuid()
              + ": "
              + e,
          e);
    }
  }

  // languages
  // see https://www.dublincore.org/specifications/dublin-core/dcmi-terms/terms/language/
  public List<DCElement> createDCLanguages(DigitalObject digitalObject) throws ServiceException {
    try {
      List<DCElement> result = new ArrayList<>();

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
    } catch (Exception e) {
      throw new ServiceException(
          "Cannot create DC Languages for DigitalObject with uuid="
              + digitalObject.getUuid()
              + ": "
              + e,
          e);
    }
  }

  // titles
  public List<DCElement> createDCTitles(DigitalObject digitalObject) throws ServiceException {
    List<DCElement> result = new ArrayList<>();
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
