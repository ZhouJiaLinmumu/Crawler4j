package edu.uci.ics.crawler4j.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.util.Net;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;

import edu.uci.ics.crawler4j.crawler.Configurable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */

public class Parser extends Configurable {

  protected static final Logger logger = LoggerFactory.getLogger(Parser.class);

  private HtmlParser htmlParser;
  private ParseContext parseContext;

  public Parser(CrawlConfig config) {
    super(config);
    htmlParser = new HtmlParser();
    parseContext = new ParseContext();
  }

  public void parse(Page page, String contextURL) throws NotAllowedContentException, ParseException {
     // �����ǰҳ���contentType��ʾ���Ƕ���������
	if (Util.hasBinaryContent(page.getContentType())) { // BINARY
      BinaryParseData parseData = new BinaryParseData();
      // �����Ҫ��ȡ����������
      if (config.isIncludeBinaryContentInCrawling()) {
        parseData.setBinaryContent(page.getContentData());
        page.setParseData(parseData);
        if (parseData.getHtml() == null) {
          throw new ParseException();
        }
        // �Ӷ����������л�ȡhtml�ı����ٴ��ı��л�ȡ��������
        parseData.setOutgoingUrls(Net.extractUrls(parseData.getHtml()));
      } else {
        throw new NotAllowedContentException();
      }
    } else if (Util.hasPlainTextContent(page.getContentType())) { // plain Text���ı�����
      try {
        TextParseData parseData = new TextParseData();
        if (page.getContentCharset() == null) { // �����ַ������õ���ҳ�ı�����
          parseData.setTextContent(new String(page.getContentData()));
        } else {
          parseData.setTextContent(new String(page.getContentData(), page.getContentCharset()));
        }
        // ��ȡ����
        parseData.setOutgoingUrls(Net.extractUrls(parseData.getTextContent()));
        page.setParseData(parseData);
      } catch (Exception e) {
        logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
        throw new ParseException();
      }
    } else { // isHTML
      Metadata metadata = new Metadata();
      HtmlContentHandler contentHandler = new HtmlContentHandler();
      try (InputStream inputStream = new ByteArrayInputStream(page.getContentData())) {
    	// ʹ��htmlparser������ҳ�ṹ
        htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
      } catch (Exception e) {
        logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
        throw new ParseException();
      }

      if (page.getContentCharset() == null) {
        page.setContentCharset(metadata.get("Content-Encoding"));
      }
      // ʹ��HtmlParseData�����н���
      HtmlParseData parseData = new HtmlParseData();
      parseData.setText(contentHandler.getBodyText().trim());
      parseData.setTitle(metadata.get(DublinCore.TITLE));
      parseData.setMetaTags(contentHandler.getMetaTags());
      // Please note that identifying language takes less than 10 milliseconds
      LanguageIdentifier languageIdentifier = new LanguageIdentifier(parseData.getText());
      page.setLanguage(languageIdentifier.getLanguage());

      Set<WebURL> outgoingUrls = new HashSet<>();

      String baseURL = contentHandler.getBaseUrl();
      if (baseURL != null) {
        contextURL = baseURL;
      }

      int urlCount = 0;
      for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {
    	// �������е�����  
        String href = urlAnchorPair.getHref();
        if (href == null || href.trim().length() == 0) {
          continue;
        }

        String hrefLoweredCase = href.trim().toLowerCase();
        if (!hrefLoweredCase.contains("javascript:") && !hrefLoweredCase.contains("mailto:") && !hrefLoweredCase.contains("@")) {
          // �淶��url����
          String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
          if (url != null) {
            WebURL webURL = new WebURL();
            webURL.setURL(url);
            webURL.setTag(urlAnchorPair.getTag());
            webURL.setAnchor(urlAnchorPair.getAnchor());
            outgoingUrls.add(webURL); // outgoingUrls�Ǹ�set����֤�˲��ظ�
            urlCount++;
            if (urlCount > config.getMaxOutgoingLinksToFollow()) { // �Ƿ񳬳����ɴ�������������
              break;
            }
          }
        }
      }
      parseData.setOutgoingUrls(outgoingUrls);

      try {
        if (page.getContentCharset() == null) {
          parseData.setHtml(new String(page.getContentData()));
        } else {
          parseData.setHtml(new String(page.getContentData(), page.getContentCharset()));
        }

        page.setParseData(parseData);
      } catch (UnsupportedEncodingException e) {
        logger.error("error parsing the html: " + page.getWebURL().getURL(), e);
        throw new ParseException();
      }
    }
  }
}