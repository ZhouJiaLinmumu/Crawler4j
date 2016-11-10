package edu.uci.ics.crawler4j.parser;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.HashSet;
import java.util.Set;

//对文本数据进行parse的类
public class TextParseData implements ParseData {

  private String textContent;
  private Set<WebURL> outgoingUrls = new HashSet<>();

  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }

  @Override
  public Set<WebURL> getOutgoingUrls() {
    return outgoingUrls;
  }

  @Override
  public void setOutgoingUrls(Set<WebURL> outgoingUrls) {
    this.outgoingUrls = outgoingUrls;
  }

  @Override
  public String toString() {
    return textContent;
  }
}