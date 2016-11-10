package edu.uci.ics.crawler4j.parser;

// 将html文本中的超链接标签，拆分为href,anchor,tag各部分
public class ExtractedUrlAnchorPair {

  private String href;
  private String anchor;
  private String tag;

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getAnchor() {
    return anchor;
  }

  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }
}