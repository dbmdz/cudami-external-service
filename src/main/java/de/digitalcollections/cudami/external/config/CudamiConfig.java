package de.digitalcollections.cudami.external.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cudami")
@Validated
public class CudamiConfig {

  private final Server server;

  public CudamiConfig(Server server) {
    this.server = server;
  }

  public Server getServer() {
    return server;
  }

  public static class Server {

    private final URI url;

    public Server(URI url) {
      this.url = url;
    }

    public URI getUrl() {
      return url;
    }
  }
}
