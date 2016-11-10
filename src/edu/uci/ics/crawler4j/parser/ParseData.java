package edu.uci.ics.crawler4j.parser;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Set;

//抽象接口类
public interface ParseData {

  //得到当前页面的所有外链
  Set<WebURL> getOutgoingUrls();

  void setOutgoingUrls(Set<WebURL> outgoingUrls);

  @Override
  String toString();
}