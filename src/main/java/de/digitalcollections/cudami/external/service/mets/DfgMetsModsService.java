package de.digitalcollections.cudami.external.service.mets;

import de.digitalcollections.cudami.external.config.DfgConfig;
import de.digitalcollections.cudami.external.config.IiifConfig;
import de.digitalcollections.cudami.external.repository.CudamiRepository;
import de.digitalcollections.cudami.external.service.mods.DfgModsService;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.LinkedDataFileResource;
import de.digitalcollections.model.legal.License;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.mycore.libmeta.dfgviewer.DVLinksXMLProcessor;
import org.mycore.libmeta.dfgviewer.DVRightsXMLProcessor;
import org.mycore.libmeta.dfgviewer.model.Links;
import org.mycore.libmeta.dfgviewer.model.Rights;
import org.mycore.libmeta.dfgviewer.model.Rights.Builder;
import org.mycore.libmeta.mets.model.Mets;
import org.mycore.libmeta.mets.model._enums.MDTYPE;
import org.mycore.libmeta.mets.model.mdsec.MdSec;
import org.mycore.libmeta.mets.model.mdsec.MdWrap;
import org.mycore.libmeta.mets.model.mdsec.XMLData;
import org.mycore.libmeta.mods.MODSXMLProcessor;
import org.mycore.libmeta.mods.model.Mods;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

/** Service for creation of DFG specific METS metadata by given (fully filled) DigitalObject. */
@SuppressFBWarnings
@Service
public class DfgMetsModsService extends MetsService {
  private final DfgConfig dfgConfig;
  private final DfgModsService dfgModsService;
  private final IiifConfig iiifConfig;

  public DfgMetsModsService(
      DfgModsService dfgModsService,
      CudamiRepository cudamiRepository,
      DfgConfig dfgConfig,
      IiifConfig iiifConfig) {
    super(cudamiRepository);
    this.dfgConfig = dfgConfig;
    this.dfgModsService = dfgModsService;
    this.iiifConfig = iiifConfig;
  }

  @Override
  public Mets getMetsForDigitalObject(DigitalObject digitalObject) throws Exception {
    Mets mets = super.getMetsForDigitalObject(digitalObject);

    // add MODS to mets:dmdSec
    Mods mods = dfgModsService.getModsForDigitalObject(digitalObject);
    Element modsElement = MODSXMLProcessor.getInstance().marshalToDOM(mods).getDocumentElement();
    MdWrap mdWrap = MdWrap.builder().MDTYPE(MDTYPE.MODS).build();
    mdWrap.setXmlData(XMLData.builder().addNode(modsElement).build());
    MdSec mdSec = MdSec.builder().mdWrap(mdWrap).build();
    mets.getDmdSec().add(mdSec);

    // add DVRIGHTS to <mets:amdSec ID="AMD">/<mets:rightsMD ID="RIGHTS">/.../dv:rights
    Rights rights = getDfgRightsForDigitalObject(digitalObject);
    Element rightsElement =
        DVRightsXMLProcessor.getInstance().marshalToDOM(rights).getDocumentElement();
    MdWrap mdWrapDVRights =
        MdWrap.builder().MDTYPE(MDTYPE.OTHER).MIMETYPE("text/xml").OTHERMDTYPE("DVRIGHTS").build();
    mdWrapDVRights.setXmlData(XMLData.builder().addNode(rightsElement).build());
    MdSec rightsMD = mets.getAmdSec().get(0).getRightsMD().get(0); // has been created before
    rightsMD.setMdWrap(mdWrapDVRights);

    // add DVLINKS to <mets:amdSec ID="AMD">/<mets:digiprovMD ID="DIGIPROV">
    Links links = getDfgLinksForDigitalObject(digitalObject);
    Element linksElement =
        DVLinksXMLProcessor.getInstance().marshalToDOM(links).getDocumentElement();
    MdWrap mdWrapDVLinks =
        MdWrap.builder().MDTYPE(MDTYPE.OTHER).MIMETYPE("text/xml").OTHERMDTYPE("DVLINKS").build();
    mdWrapDVLinks.setXmlData(XMLData.builder().addNode(linksElement).build());
    MdSec digiprovMD = mets.getAmdSec().get(0).getDigiprovMD().get(0); // has been created before
    digiprovMD.setMdWrap(mdWrapDVLinks);

    return mets;
  }

  /*
   * Example:
   *
   * <dv:links>
   *   <dv:reference linktext=”OPAC”>http://slub-dresden.de/FOZK.pl?PPN=356448053</dv:reference>
   *   <dv:reference linktext=”WorldCat”>http://worldcat.org/search?356448053</dv:reference>
   *   <dv:presentation>http://slub-dresden.de/356448053</dv:presentation>
   *   <dv:sru>http://digital.slub-dresden.de/sru/356448053</dv:sru>
   *   <dv:iiif>http://digital.slub-dresden.de/iiif/356448053.xml</dv:iiif>
   * </dv:links>
   */
  private Links getDfgLinksForDigitalObject(DigitalObject digitalObject) {
    org.mycore.libmeta.dfgviewer.model.Links.Builder linksBuilder = Links.builder();

    // mandatory fields
    // dv:reference (catalogue)
    // TODO

    // optional fields
    // dv:presentation
    // TODO

    // dv:sru
    // TODO

    // dv:iiif
    String iiifPresentationManifestUrl = getIiifPresentationManifestUrl(digitalObject);
    if (iiifPresentationManifestUrl != null) {
      linksBuilder.iiif(iiifPresentationManifestUrl);
    }

    return linksBuilder.build();
  }

  /*
   * Example:
   *
   * <dv:rights>
   *   <dv:owner>SLUB Dresden</dv:owner>
   *   <dv:ownerLogo>http://digital.slub-dresden.de/logo.gif</dv:ownerLogo>
   *   <dv:ownerSiteURL>http://digital.slub-dresden.de/</dv:ownerSiteURL>
   *   <dv:ownerContact>mailto:sebastian.meyer@slub-dresden.de</dv:ownerContact>
   *   <dv:license>cc-by</dv:license>
   * </dv:rights>
   */
  private Rights getDfgRightsForDigitalObject(DigitalObject digitalObject) {
    // mandatory fields
    Builder rightsBuilder =
        Rights.builder()
            .owner(dfgConfig.getRights().getOwner())
            .ownerContact(dfgConfig.getRights().getOwnerContact())
            .ownerLogo(dfgConfig.getRights().getOwnerLogo().toString())
            .ownerSiteURL(dfgConfig.getRights().getOwnerSiteUrl().toString());

    // optional fields
    // dv:aggregator, dv:aggregatorLogo, dv:aggregatorSiteURL
    // TODO

    // dv:sponsor, dv:sponsorLogo, dv:sponsorSiteURL
    // TODO

    // dv:license
    License license = digitalObject.getLicense();
    if (license != null) {
      URL licenseUrl = license.getUrl();
      if (licenseUrl != null) {
        rightsBuilder.license(licenseUrl.toString());
      }
    }

    return rightsBuilder.build();
  }

  public String getIiifPresentationManifestUrl(DigitalObject digitalObject) {
    String result = null;
    try {
      // try to get stored IIIF-Manifest-Url
      List<LinkedDataFileResource> linkedDataResources = digitalObject.getLinkedDataResources();
      if (linkedDataResources != null) {
        Optional<LinkedDataFileResource> ldfrOpt =
            linkedDataResources.stream()
                .filter(
                    ldfr ->
                        ldfr.getContext() != null
                            && ("http://iiif.io/api/presentation/2/context.json"
                                    .equals(ldfr.getContext().toString())
                                || "http://iiif.io/api/presentation/3/context.json"
                                    .equals(ldfr.getContext().toString())))
                .findAny();
        if (ldfrOpt.isPresent()) {
          result = ldfrOpt.get().getUri().toString();
        }
      }

      // if not found, create one (fallback)
      if (result == null) {
        URI iiifPresentationBaseUrl = iiifConfig.getPresentation().getBaseUrl();

        // default: iiif identifier = uuid
        String iiifIdentifier = digitalObject.getUuid().toString();

        // custom: get iiif identifier from one of the identifiers of the digital object
        List<String> identifierNamespaces = iiifConfig.getIdentifier().getNamespaces();
        for (String identifierNamespace : identifierNamespaces) {
          Identifier identifier = digitalObject.getIdentifierByNamespace(identifierNamespace);
          if (identifier != null) {
            iiifIdentifier = identifier.getId();
            break;
          }
        }

        URL iiifManifestUrl = iiifPresentationBaseUrl.resolve(iiifIdentifier + "/manifest").toURL();
        result = iiifManifestUrl.toString();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("can not get/create IIIF presentation URL for digital object", e);
    }
    return result;
  }
}
