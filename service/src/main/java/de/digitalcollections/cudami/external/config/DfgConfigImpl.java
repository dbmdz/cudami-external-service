package de.digitalcollections.cudami.external.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "dfg")
@Validated
public class DfgConfigImpl implements DfgConfig {

  private Rights rights;

  public DfgConfigImpl() {}

  public DfgConfigImpl(Rights rights) {
    this.rights = rights;
  }

  public void setRights(Rights rights) {
    this.rights = rights;
  }

  public Rights getRights() {
    return rights;
  }

  public static class Rights implements DfgConfig.Rights {

    private String owner;
    private String ownerContact;
    private URI ownerLogo;
    private URI ownerSiteUrl;

    public Rights(String owner, String ownerContact, URI ownerLogo, URI ownerSiteUrl) {
      this.owner = owner;
      this.ownerContact = ownerContact;
      this.ownerLogo = ownerLogo;
      this.ownerSiteUrl = ownerSiteUrl;
    }

    @Override
    public String getOwner() {
      return owner;
    }

    @Override
    public String getOwnerContact() {
      return ownerContact;
    }

    @Override
    public URI getOwnerLogo() {
      return ownerLogo;
    }

    @Override
    public URI getOwnerSiteUrl() {
      return ownerSiteUrl;
    }
  }
}
