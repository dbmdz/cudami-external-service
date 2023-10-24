package de.digitalcollections.cudami.external.config;

import java.net.URI;

public interface DfgConfig {

  public static interface Rights {

    public String getOwner();

    public String getOwnerContact();

    public URI getOwnerLogo();

    public URI getOwnerSiteUrl();
  }

  public Rights getRights();
}
