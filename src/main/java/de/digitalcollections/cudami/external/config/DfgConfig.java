package de.digitalcollections.cudami.external.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "dfg")
@Validated
public class DfgConfig {

  public static class Rights {

    private final String owner;
    private final String ownerContact;
    private final URI ownerLogo;
    private final URI ownerSiteUrl;

    public Rights(String owner, String ownerContact, URI ownerLogo, URI ownerSiteUrl) {
      this.owner = owner;
      this.ownerContact = ownerContact;
      this.ownerLogo = ownerLogo;
      this.ownerSiteUrl = ownerSiteUrl;
    }

    public String getOwner() {
      return owner;
    }

    public String getOwnerContact() {
      return ownerContact;
    }

    public URI getOwnerLogo() {
      return ownerLogo;
    }

    public URI getOwnerSiteUrl() {
      return ownerSiteUrl;
    }
  }

  private final Rights rights;

  public DfgConfig(Rights rights) {
    this.rights = rights;
  }

  public Rights getRights() {
    return rights;
  }
}
