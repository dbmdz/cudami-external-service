package de.digitalcollections.cudami.external.service;

public class ServiceException extends Exception {

  public ServiceException(String msg, Exception e) {
    super(msg, e);
  }

  public ServiceException(String msg) {
    super(msg);
  }
}
