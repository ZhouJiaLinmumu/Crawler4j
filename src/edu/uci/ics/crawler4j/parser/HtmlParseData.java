package edu.uci.ics.crawler4j.parser;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Map;
import java.util.Set;

// 用于处理html页面的parse类
public class HtmlParseData implements ParseData {

  private String html;
  private String text;
  private String title;
  private Map<String, String> metaTags;

  private Set<WebURL> outgoingUrls;

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Map<String, String> getMetaTags() {
    return metaTags;
  }

  public void setMetaTags(Map<String, String> metaTags) {
    this.metaTags = metaTags;
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
    return text;
  }
}