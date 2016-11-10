package edu.uci.ics.crawler4j.crawler;

import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * This class contains the data for a fetched and parsed page.
 *
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
// 用来描述web页面的类
public class Page {

  /**
   * The URL of this page.
   */
  // 当前页面的url
  protected WebURL url;

  /**
  * Redirection flag
  */
  // 当前页面是否重定向
  protected boolean redirect;

  /**
   * The URL to which this page will be redirected to
   */
  // 重定向的url
  protected String redirectedToUrl;

  /**
  * Status of the page
  */
  // 当前页面的状态码
  protected int statusCode;

  /**
   * The content of this page in binary format.
   */
  // 二进制格式的页面内容
  protected byte[] contentData;

  /**
   * The ContentType of this page.
   * For example: "text/html; charset=UTF-8"
   */
  // 当前页面的contentType
  protected String contentType;

  /**
   * The encoding of the content.
   * For example: "gzip"
   */
  // 当前页面的编码方式
  protected String contentEncoding;

  /**
   * The charset of the content.
   * For example: "UTF-8"
   */
  // 页面内容的字符集
  protected String contentCharset;

  /**
  * Language of the Content.
  */
  // 页面内容的language
  private String language;

  /**
   * Headers which were present in the response of the fetch request
   */
  // 当前页面response中的header集合
  protected Header[] fetchResponseHeaders;

  /**
   * The parsed data populated by parsers
   */
  // 使用parser翻译过后的页面
  protected ParseData parseData;


  public Page(WebURL url) {
    this.url = url;
  }

  /**
   * Loads the content of this page from a fetched HttpEntity.
   *
   * @param entity HttpEntity
   * @throws Exception when load fails
   */
  // 解析通过httpclient包收到的entity
  public void load(HttpEntity entity) throws Exception {

    contentType = null;
    Header type = entity.getContentType();
    if (type != null) {
      contentType = type.getValue();
    }

    contentEncoding = null;
    Header encoding = entity.getContentEncoding();
    if (encoding != null) {
      contentEncoding = encoding.getValue();
    }

    Charset charset = ContentType.getOrDefault(entity).getCharset();
    if (charset != null) {
      contentCharset = charset.displayName();
    }

    contentData = EntityUtils.toByteArray(entity);
  }

  public WebURL getWebURL() {
    return url;
  }

  public void setWebURL(WebURL url) {
    this.url = url;
  }

  public boolean isRedirect() {
    return redirect;
  }

  public void setRedirect(boolean redirect) {
    this.redirect = redirect;
  }

  public String getRedirectedToUrl() {
    return redirectedToUrl;
  }

  public void setRedirectedToUrl(String redirectedToUrl) {
    this.redirectedToUrl = redirectedToUrl;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Returns headers which were present in the response of the fetch request
   *
   * @return Header Array, the response headers
   */
  public Header[] getFetchResponseHeaders() {
    return fetchResponseHeaders;
  }

  public void setFetchResponseHeaders(Header[] headers) {
    fetchResponseHeaders = headers;
  }

  /**
   * @return parsed data generated for this page by parsers
   */
  public ParseData getParseData() {
    return parseData;
  }

  public void setParseData(ParseData parseData) {
    this.parseData = parseData;
  }

  /**
   * @return content of this page in binary format.
   */
  public byte[] getContentData() {
    return contentData;
  }

  public void setContentData(byte[] contentData) {
    this.contentData = contentData;
  }

  /**
   * @return ContentType of this page.
   * For example: "text/html; charset=UTF-8"
   */
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * @return encoding of the content.
   * For example: "gzip"
   */
  public String getContentEncoding() {
    return contentEncoding;
  }

  public void setContentEncoding(String contentEncoding) {
    this.contentEncoding = contentEncoding;
  }

  /**
   * @return charset of the content.
   * For example: "UTF-8"
   */
  public String getContentCharset() {
    return contentCharset;
  }

  public void setContentCharset(String contentCharset) {
    this.contentCharset = contentCharset;
  }

  /**
   * @return Language
   */
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}