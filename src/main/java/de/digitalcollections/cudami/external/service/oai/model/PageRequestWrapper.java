package de.digitalcollections.cudami.external.service.oai.model;

import de.digitalcollections.model.list.paging.PageRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mycore.oai.pmh.Set;

@SuppressFBWarnings
public class PageRequestWrapper {
  private MetadataFormatWrapper metadataFormatWrapper;
  private PageRequest pageRequest;
  private Set set;

  public PageRequestWrapper() {}

  public MetadataFormatWrapper getMetadataFormatWrapper() {
    return metadataFormatWrapper;
  }

  public PageRequest getPageRequest() {
    return pageRequest;
  }

  public Set getSet() {
    return set;
  }

  public void setMetadataFormatWrapper(MetadataFormatWrapper metadataFormatWrapper) {
    this.metadataFormatWrapper = metadataFormatWrapper;
  }

  public void setPageRequest(PageRequest pageRequest) {
    this.pageRequest = pageRequest;
  }

  public void setSet(Set set) {
    this.set = set;
  }
}
