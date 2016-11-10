package edu.uci.ics.crawler4j.crawler;

/**
 * Several core components of crawler4j extend this class
 * to make them configurable.
 *
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
// �������õĳ����࣬������һЩ����Ҫʹ�õ���Щ����
public abstract class Configurable {

  protected CrawlConfig config;

  protected Configurable(CrawlConfig config) {
    this.config = config;
  }

  public CrawlConfig getConfig() {
    return config;
  }
}