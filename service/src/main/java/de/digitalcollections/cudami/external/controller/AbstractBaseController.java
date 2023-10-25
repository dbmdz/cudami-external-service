package de.digitalcollections.cudami.external.controller;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractBaseController {

  protected String replaceBaseUrl(String xml, HttpServletRequest req) {
    if (xml == null) {
      return null;
    }

    String contextPath = req.getContextPath();
    String replacement = req.getRequestURL().toString().replace(req.getRequestURI(), contextPath);
    return xml.replaceAll("\\{\\{baseurl}}", replacement);
  }
}
