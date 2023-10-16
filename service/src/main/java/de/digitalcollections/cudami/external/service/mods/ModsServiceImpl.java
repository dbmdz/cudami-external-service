package de.digitalcollections.cudami.external.service.mods;

import com.datazuul.language.iso639.ISO639Language;
import com.datazuul.language.iso639.ISO639Languages;
import de.digitalcollections.cudami.external.service.ServiceException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import org.mycore.libmeta.mods.model.Mods;
import org.mycore.libmeta.mods.model.ModsVersion;
import org.mycore.libmeta.mods.model._misc.CodeOrText;
import org.mycore.libmeta.mods.model._misc.DateEncoding;
import org.mycore.libmeta.mods.model._misc.enums.LanguageTermAuthority;
import org.mycore.libmeta.mods.model._misc.enums.RelatedItemType;
import org.mycore.libmeta.mods.model._misc.enums.Yes;
import org.mycore.libmeta.mods.model._toplevel.*;
import org.mycore.libmeta.mods.model._toplevel.OriginInfo.Builder;
import org.mycore.libmeta.mods.model.language.LanguageTerm;
import org.mycore.libmeta.mods.model.location.ShelfLocator;
import org.mycore.libmeta.mods.model.origininfo.DateIssued;
import org.mycore.libmeta.mods.model.origininfo.Place;
import org.mycore.libmeta.mods.model.origininfo.place.PlaceTerm;
import org.mycore.libmeta.mods.model.physicaldescription.DigitalOrigin;
import org.mycore.libmeta.mods.model.titleInfo.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsServiceImpl implements ModsItemsService, ModsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModsServiceImpl.class);

  public Identifier createIdentifierPurl(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("purl").build();
    return identifier;
  }

  public Identifier createIdentifierUrn(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("urn").build();
    return identifier;
  }

  public Location createLocation(DigitalObject digitalObject) {
    // mods:shelfLocator
    ShelfLocator shelfLocator = createShelfLocator(digitalObject);

    Location location = Location.builderForLocation().addShelfLocator(shelfLocator).build();
    return location;
  }

  public OriginInfo createOriginInfo(DigitalObject digitalObject) {
    Builder originInfoBuilder = OriginInfo.builderForOriginInfo();

    Item item = digitalObject.getItem();
    if (item != null && item.getExemplifiesManifestation()) {
      Manifestation manifestation = item.getManifestation();
      if (manifestation != null) {
        // mods:dateIssued
        DateIssued dateIssued = createDateIssued(manifestation);
        if (dateIssued != null) {
          originInfoBuilder.addContent(dateIssued);
        }

        // mods:place/mods:placeTerm
        PlaceTerm placeTerm = PlaceTerm.builderForPlaceTerm().type(CodeOrText.TEXT).build();
        Place place = Place.builderForPlace().addContent(placeTerm).build();
        originInfoBuilder.addContent(place).build();
        // TODO
      }
    }

    return originInfoBuilder.build();
  }

  /*
   * see w3cdtf = https://www.w3.org/TR/NOTE-datetime
   * see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE
   * see DateTimeFormatter ISO_LOCAL_DATE as '2011-12-03'
   */
  private DateIssued createDateIssued(Manifestation manifestation) {
    DateIssued dateIssued = null;
    try {
      LocalDate publicationDate = manifestation.getPublicationInfo().getNavDateRange().getStart();
      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
      String text = publicationDate.format(formatter);
      dateIssued =
          DateIssued.builderForDateIssued()
              .content(text)
              .encoding(DateEncoding.W3CDTF)
              .keyDate(Yes.YES)
              .build();
    } catch (Exception e) {
      LOGGER.error("Error at creating date issued from manifestation data", e);
    }
    return dateIssued;
  }

  public PhysicalDescription createPhysicalDescription(DigitalObject digitalObject) {
    PhysicalDescription physicalDescription =
        PhysicalDescription.builderForPhysicalDescription()
            .addContent(DigitalOrigin.REFORMATTED_DIGITAL)
            .build();
    return physicalDescription;
  }

  public RelatedItem createRelatedItem(DigitalObject digitalObject) {
    RelatedItem.Builder relatedItemBuilder =
        RelatedItem.builderForRelatedItem().type(RelatedItemType.HOST);

    Item item = null;
    Manifestation manifestation = null;
    Title title;
    TitleInfo.Builder titleInfoBuilder = TitleInfo.builder();

    // mods:relatedItem
    item = digitalObject.getItem();
    if (item != null && item.getExemplifiesManifestation()) {
      manifestation = item.getManifestation();
      if (manifestation != null) {
        // mods:relatedItem/titleInfo/title
        title =
            Title.builder().content(manifestation.getTitles().get(0).getText().getText()).build();
        titleInfoBuilder.addContent(title);

        // mods:relatedItem/language/languageTerm  authority="iso639-2b" type="code": (e.g. "ger")
        Locale locale = manifestation.getLanguage();
        String lang = locale.getISO3Language();
        ISO639Language iso639Language = ISO639Languages.getByLocale(locale);
        if (iso639Language != null) {
          lang = iso639Language.getPart2B();
        }

        Language.Builder languageBuilder = Language.builderForLanguage();
        LanguageTerm languageTerm =
            LanguageTerm.builderForLanguaeTerm()
                .authority(LanguageTermAuthority.ISO639_2B)
                .type(CodeOrText.CODE)
                .content(lang)
                .build();
        Language language = languageBuilder.addLanguageTerm(languageTerm).build();
        relatedItemBuilder.addContent(language);

        System.out.println(manifestation.getCreated());
      }
    }

    // Fallback (TODO: delete if item and manifestation are always present/filled)
    if (item == null || manifestation == null) {
      // mods:relatedItem/titleInfo/title
      title = Title.builder().content(digitalObject.getLabel().getText()).build();
      titleInfoBuilder.addContent(title);
    }

    // mods:relatedItem/recordInfo
    RecordInfo recordInfo = RecordInfo.builderForRecordInfo().build();
    // mods:relatedItem/recordInfo/recordIdentifier
    // TODO

    TitleInfo titleInfo = titleInfoBuilder.build();

    RelatedItem relatedItem =
        relatedItemBuilder.addContent(titleInfo).addContent(recordInfo).build();
    return relatedItem;
  }

  public ShelfLocator createShelfLocator(DigitalObject digitalObject) {
    ShelfLocator shelfLocator = null;
    Item item = digitalObject.getItem();
    if (item != null) {
      de.digitalcollections.model.identifiable.Identifier shelfNo =
          item.getIdentifierByNamespace("shelfno");
      if (shelfNo != null) {
        shelfLocator = ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
      }
    }
    return shelfLocator;
  }

  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws ServiceException {

    // mods:location
    Location location = createLocation(digitalObject);

    // mods:relatedItem
    RelatedItem relatedItem = createRelatedItem(digitalObject);

    // mods:physicalDescription
    PhysicalDescription physicalDescription = createPhysicalDescription(digitalObject);

    // mods:identifier type="purl"
    Identifier identifierPurl = createIdentifierPurl(digitalObject);

    // mods:identifier type="urn"
    Identifier identifierUrn = createIdentifierUrn(digitalObject);

    // mods:originInfo
    OriginInfo originInfo = createOriginInfo(digitalObject);

    Mods mods =
        Mods.builder()
            .version(ModsVersion.VERSION_3_5)
            .addContent(location)
            .addContent(relatedItem)
            .addContent(physicalDescription)
            .addContent(identifierPurl)
            .addContent(identifierUrn)
            .addContent(originInfo)
            .build();
    return mods;
  }
}
