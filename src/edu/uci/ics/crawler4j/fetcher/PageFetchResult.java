package edu.uci.ics.crawler4j.fetcher;

import java.io.EOFException;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
// 使用httpclient包fetch page之后存储的结果
public class PageFetchResult {

  protected static final Logger logger = LoggerFactory.getLogger(PageFetchResult.class);

  protected int statusCode;		// 状态码
  protected HttpEntity entity = null;	// httpEntity对象
  protected Header[] responseHeaders = null;	// 响应消息头
  protected String fetchedUrl = null;	// url链接
  protected String movedToUrl = null;

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public HttpEntity getEntity() {
    return entity;
  }

  public void setEntity(HttpEntity entity) {
    this.entity = entity;
  }

  public Header[] getResponseHeaders() {
    return responseHeaders;
  }

  public void setResponseHeaders(Header[] responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  public String getFetchedUrl() {
    return fetchedUrl;
  }

  public void setFetchedUrl(String fetchedUrl) {
    this.fetchedUrl = fetchedUrl;
  }

  public boolean fetchContent(Page page) {
    try {
      // 将fetch后的结果解析转换成page对象	
      page.load(entity);
      page.setFetchResponseHeaders(responseHeaders);
      return true;
    } catch (Exception e) {
      logger.info("Exception while fetching content for: {} [{}]", page.getWebURL().getURL(), e.getMessage());
    }
    return false;
  }

  // 忽略content，不进行处理
  public void discardContentIfNotConsumed() {
    try {
      if (entity != null) {
        EntityUtils.consume(entity);
      }
    } catch (IOException e) {
      // We can EOFException (extends IOException) exception. It can happen on compressed streams which are not repeatable
      // We can ignore this exception. It can happen if the stream is closed.
    } catch (Exception e) {
      logger.warn("Unexpected error occurred while trying to discard content", e);
    }
  }

  public String getMovedToUrl() {
    return movedToUrl;
  }

  public void setMovedToUrl(String movedToUrl) {
    this.movedToUrl = movedToUrl;
  }
}