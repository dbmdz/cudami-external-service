package de.digitalcollections.cudami.external.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "oai")
@Validated
public class OaiConfig {

  public static class Identify {

    private final String adminEmail;
    private final String baseUrl;
    private final String repositoryIdentifier;
    private final String repositoryName;
    private final String sampleId;

    public Identify(
        String adminEmail,
        String baseUrl,
        String repositoryIdentifier,
        String repositoryName,
        String sampleId) {
      this.adminEmail = adminEmail;
      this.baseUrl = baseUrl;
      this.repositoryIdentifier = repositoryIdentifier;
      this.repositoryName = repositoryName;
      this.sampleId = sampleId;
    }

    public String getAdminEmail() {
      return adminEmail;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public String getRepositoryIdentifier() {
      return repositoryIdentifier;
    }

    public String getRepositoryName() {
      return repositoryName;
    }

    public String getSampleId() {
      return sampleId;
    }
  }

  private final Identify identify;

  public OaiConfig(Identify identify) {
    this.identify = identify;
  }

  public Identify getIdentify() {
    return identify;
  }
}
