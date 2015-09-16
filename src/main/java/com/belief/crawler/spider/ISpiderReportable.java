package com.belief.crawler.spider;

import java.net.URL;

interface ISpiderReportable {
  public boolean spiderFoundURL(URL base,URL url);
  public void spiderURLError(URL url);
  public void spiderFoundEMail(String email);
  public int getWebDepth();
  public String getWebType();
  public int getWebSize();
}