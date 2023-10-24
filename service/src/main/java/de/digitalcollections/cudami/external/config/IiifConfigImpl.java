package de.digitalcollections.cudami.external.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "iiif")
@SuppressFBWarnings
public class IiifConfigImpl implements IiifConfig {

  public static class Identifier implements IiifConfig.Identifier {

    private List<String> namespaces;

    public Identifier() {}

    public Identifier(List<String> namespaces) {
      this.namespaces = namespaces;
    }

    public List<String> getNamespaces() {
      return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
      this.namespaces = namespaces;
    }
  }

  public static class Presentation implements IiifConfig.Presentation {

    private URI baseUrl;

    public Presentation() {}

    public Presentation(URI baseUrl) {
      this.baseUrl = baseUrl;
    }

    public URI getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
      this.baseUrl = baseUrl;
    }
  }

  private Identifier identifier;

  private Presentation presentation;

  public IiifConfigImpl() {}

  public IiifConfigImpl(Identifier identifier, Presentation presentation) {
    this.identifier = identifier;
    this.presentation = presentation;
  }

  public Identifier getIdentifier() {
    return identifier;
  }

  public Presentation getPresentation() {
    return presentation;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public void setPresentation(Presentation presentation) {
    this.presentation = presentation;
  }
}
