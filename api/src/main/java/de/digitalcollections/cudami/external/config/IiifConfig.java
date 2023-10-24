package de.digitalcollections.cudami.external.config;

import java.net.URI;
import java.util.List;

public interface IiifConfig {
  public Presentation getPresentation();

  public Identifier getIdentifier();

  public static interface Presentation {
    public URI getBaseUrl();
  }

  public static interface Identifier {
    public List<String> getNamespaces();
  }
}
