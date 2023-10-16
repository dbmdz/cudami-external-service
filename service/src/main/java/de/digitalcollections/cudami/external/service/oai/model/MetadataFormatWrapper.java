package de.digitalcollections.cudami.external.service.oai.model;

import org.mycore.oai.pmh.MetadataFormat;

public class MetadataFormatWrapper {
  private String namespaceURI;
  private String prefix;
  private String schema;

  public MetadataFormatWrapper(MetadataFormat metadataFormat) {
    namespaceURI = metadataFormat.getNamespace();
    prefix = metadataFormat.getPrefix();
    schema = metadataFormat.getSchema();
  }

  public MetadataFormatWrapper() {}

  public MetadataFormat getMetadataFormat() {
    return new MetadataFormat(prefix, namespaceURI, schema);
  }

  public String getNamespaceURI() {
    return namespaceURI;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSchema() {
    return schema;
  }

  public void setNamespaceURI(String namespaceURI) {
    this.namespaceURI = namespaceURI;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }
}
