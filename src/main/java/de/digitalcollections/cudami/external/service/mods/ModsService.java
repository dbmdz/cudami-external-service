package de.digitalcollections.cudami.external.service.mods;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import java.time.LocalDate;
import java.util.Locale;
import org.mycore.libmeta.mods.model.Mods;
import org.mycore.libmeta.mods.model.ModsVersion;
import org.mycore.libmeta.mods.model._misc.CodeOrText;
import org.mycore.libmeta.mods.model._misc.DateEncoding;
import org.mycore.libmeta.mods.model._misc.enums.LanguageTermAuthority;
import org.mycore.libmeta.mods.model._misc.enums.RelatedItemType;
import org.mycore.libmeta.mods.model._misc.enums.Yes;
import org.mycore.libmeta.mods.model._toplevel.*;
import org.mycore.libmeta.mods.model.language.LanguageTerm;
import org.mycore.libmeta.mods.model.location.ShelfLocator;
import org.mycore.libmeta.mods.model.origininfo.DateIssued;
import org.mycore.libmeta.mods.model.origininfo.Place;
import org.mycore.libmeta.mods.model.origininfo.place.PlaceTerm;
import org.mycore.libmeta.mods.model.physicaldescription.DigitalOrigin;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsService {
  protected Identifier createIdentifierPurl(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("purl").build();
    return identifier;
  }

  protected Identifier createIdentifierUrn(DigitalObject digitalObject) {
    // TODO

    Identifier identifier = Identifier.builderForIdentifier().type("urn").build();
    return identifier;
  }

  protected Location createLocation(DigitalObject digitalObject) {
    // mods:shelfLocator
    ShelfLocator shelfLocator = createShelfLocator(digitalObject);

    Location location = Location.builderForLocation().addShelfLocator(shelfLocator).build();
    return location;
  }

  protected OriginInfo createOriginInfo(DigitalObject digitalObject) {
    Item item = digitalObject.getItem();
    if (item.getExemplifiesManifestation()) {
      Manifestation manifestation = item.getManifestation();
      if (manifestation != null) {
        System.out.println(manifestation.getCreated());

        LocalDate publicationDate = manifestation.getPublicationInfo().getNavDateRange().getStart();
      }
    }

    // mods:place/mods:placeTerm
    PlaceTerm placeTerm = PlaceTerm.builderForPlaceTerm().type(CodeOrText.TEXT).build();
    Place place = Place.builderForPlace().addContent(placeTerm).build();
    // TODO

    // mods:dateIssued
    // see w3cdtf = https://www.w3.org/TR/NOTE-datetime
    // see
    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE
    // DateTimeFormatter ISO_LOCAL_DATE as '2011-12-03'
    /*
       * LocalDate date = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    String text = date.format(formatter);
       */
    DateIssued dateIssued =
        DateIssued.builderForDateIssued().encoding(DateEncoding.W3CDTF).keyDate(Yes.YES).build();
    // TODO

    OriginInfo originInfo =
        OriginInfo.builderForOriginInfo().addContent(place).addContent(dateIssued).build();
    return originInfo;
  }

  protected PhysicalDescription createPhysicalDescription(DigitalObject digitalObject) {
    PhysicalDescription physicalDescription =
        PhysicalDescription.builderForPhysicalDescription()
            .addContent(DigitalOrigin.REFORMATTED_DIGITAL)
            .build();
    return physicalDescription;
  }

  protected RelatedItem createRelatedItem(DigitalObject digitalObject) {
    Item item = digitalObject.getItem();
    if (item.getExemplifiesManifestation()) {
      Manifestation manifestation = item.getManifestation();
      if (manifestation != null) {
        // FIXME raus damit!
        System.out.println(manifestation.getCreated());

        Locale locale = manifestation.getLanguage();
      }
    }
    // mods:titleInfo
    TitleInfo titleInfo = TitleInfo.builder().build();
    // TODO

    // mods:language/mods:languageTerm authority="iso639-2b" type="code"
    LanguageTerm languageTerm =
        LanguageTerm.builderForLanguaeTerm()
            .authority(LanguageTermAuthority.ISO639_2B)
            .type(CodeOrText.CODE)
            .build();
    Language language = Language.builderForLanguage().addLanguageTerm(languageTerm).build();
    // TODO

    // mods:recordInfo
    RecordInfo recordInfo = RecordInfo.builderForRecordInfo().build();
    // mods:recordIdentifier
    // TODO

    RelatedItem relatedItem =
        RelatedItem.builderForRelatedItem()
            .type(RelatedItemType.HOST)
            .addContent(titleInfo)
            .addContent(language)
            .addContent(recordInfo)
            .build();
    return relatedItem;
  }

  protected ShelfLocator createShelfLocator(DigitalObject digitalObject) {
    ShelfLocator shelfLocator = null;
    Item item = digitalObject.getItem();
    de.digitalcollections.model.identifiable.Identifier shelfNo =
        item.getIdentifierByNamespace("shelfno");
    if (shelfNo != null) {
      shelfLocator = ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
    }
    return shelfLocator;
  }

  public Mods getModsForDigitalObject(DigitalObject digitalObject) throws Exception {

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
