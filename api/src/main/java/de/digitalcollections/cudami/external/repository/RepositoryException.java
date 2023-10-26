package de.digitalcollections.cudami.external.repository;

public class RepositoryException extends Exception {
  public RepositoryException(String msg, Exception e) {
    super(msg, e);
  }

  public RepositoryException(String msg) {
    super(msg);
  }
}
