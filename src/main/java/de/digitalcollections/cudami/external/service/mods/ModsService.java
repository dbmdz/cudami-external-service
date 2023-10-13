package de.digitalcollections.cudami.external.service.mods;

import com.datazuul.language.iso639.ISO639Languages;
import de.digitalcollections.cudami.external.util.TitleUtil;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import java.time.LocalDate;
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
import org.mycore.libmeta.mods.model.titleInfo.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service for creation of METS metadata by given (fully filled) DigitalObject. */
@Service
public class ModsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModsService.class);

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
    RelatedItem.Builder relatedItemBuilder =
        RelatedItem.builderForRelatedItem().type(RelatedItemType.HOST);

    Item item = digitalObject.getItem();
    Manifestation manifestation = null;

    if (item.getExemplifiesManifestation()) {
      manifestation = item.getManifestation();
      Manifestation titleManifestation = manifestation;
      if (titleManifestation.getParents() != null && !titleManifestation.getParents().isEmpty()) {
        // Use the first (and only) parent manifestation, which is the manifestation of the
        // newspaper title
        titleManifestation = titleManifestation.getParents().get(0).getSubject();
      }

      // mods:titleInfo
      TitleInfo titleInfo =
          TitleInfo.builder()
              .addContent(
                  Title.builder()
                      .content(TitleUtil.filterTitle(titleManifestation, "main", "main"))
                      .build())
              .build();
      relatedItemBuilder = relatedItemBuilder.addContent(titleInfo);

      // mods:language/mods:languageTerm authority="iso639-2b" type="code"
      LanguageTerm languageTerm =
          LanguageTerm.builderForLanguaeTerm()
              .authority(LanguageTermAuthority.ISO639_2B)
              .type(CodeOrText.CODE)
              .content(ISO639Languages.getByLocale(manifestation.getLanguage()).getPart2B())
              .build();
      Language language = Language.builderForLanguage().addLanguageTerm(languageTerm).build();
      relatedItemBuilder = relatedItemBuilder.addContent(language);
    }

    // mods:recordInfo
    RecordInfo recordInfo = RecordInfo.builderForRecordInfo().build();
    // mods:recordIdentifier
    // TODO

    RelatedItem relatedItem = relatedItemBuilder.addContent(recordInfo).build();
    return relatedItem;
  }

  protected ShelfLocator createShelfLocator(DigitalObject digitalObject) {
    Item item = digitalObject.getItem();
    de.digitalcollections.model.identifiable.Identifier shelfNo =
        item.getIdentifierByNamespace("shelfno");
    if (shelfNo != null) {
      return ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
    } else {
      if (item.getPartOfItem() != null) {
        shelfNo = item.getPartOfItem().getIdentifierByNamespace("shelfno");
        if (shelfNo != null) {
          return ShelfLocator.builderForShelfLocator().content(shelfNo.getId()).build();
        }
      }
    }

    LOGGER.warn(
        "Found no shelfno identifier for item "
            + item.getUuid()
            + ", neither in item, nor in surropunding item");
    return null;
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
