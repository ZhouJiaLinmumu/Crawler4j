package edu.uci.ics.crawler4j.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 使用sax解析器
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlContentHandler extends DefaultHandler {

  private final int MAX_ANCHOR_LENGTH = 100;

  private enum Element {
    A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY
  }

  // 建立string类型的标签名到其对应的Element之间的映射关系
  private static class HtmlFactory {
    private static Map<String, Element> name2Element;

    static {
      name2Element = new HashMap<>();
      for (Element element : Element.values()) {
    	//建立字符串到enum变量的映射
        name2Element.put(element.toString().toLowerCase(), element);
      }
    }

    public static Element getElement(String name) {
      return name2Element.get(name);
    }
  }

  private String base;	// 当前网页的base标签的href的值，是当前页面的basePath
  private String metaRefresh;	// meta标签，http-equiv=refresh
  private String metaLocation;	// meta标签，http-equiv="location"
  private Map<String, String> metaTags = new HashMap<>();	// 网页的meta标签键值对（可能有多个）

  private boolean isWithinBodyElement; // 是不是在body标签之内
  private StringBuilder bodyText;	// body标签内包含的文本内容

  private List<ExtractedUrlAnchorPair> outgoingUrls; // 外链集合

  private ExtractedUrlAnchorPair curUrl = null;	// 当前处理的url所生成的ExtractedUrlAnchorPair对象
  private boolean anchorFlag = false;			// 是否有anchor
  private StringBuilder anchorText = new StringBuilder();	// 锚文本

  public HtmlContentHandler() {
    isWithinBodyElement = false;
    bodyText = new StringBuilder();
    outgoingUrls = new ArrayList<>();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    // 根据字符串，获取对应的element
	Element element = HtmlFactory.getElement(localName);

	// 如果是a,area,link标签，得到标签中的href超链接地址,并加入到outgoingUrls集合中。并设置anchorFlag为true，表示有锚文本。
    // 如<a href="example.com">A sample anchor</a>中，anchor='A sample anchor'，href=‘example.com’,tag='a'
	if (element == Element.A || element == Element.AREA || element == Element.LINK) {
      String href = attributes.getValue("href");
      if (href != null) {
        anchorFlag = true;
        addToOutgoingUrls(href, localName);

      }
    } else if (element == Element.IMG) { 	// 如果为img标签，则获取其中的src，加入到外链集合
      String imgSrc = attributes.getValue("src");
      if (imgSrc != null) {
        addToOutgoingUrls(imgSrc, localName);

      }
    } else if (element == Element.IFRAME || element == Element.FRAME || element == Element.EMBED) {
      String src = attributes.getValue("src");	// 如果为iframe,frame,embed标签，获取src，加入到外链集合
      if (src != null) {
        addToOutgoingUrls(src, localName);

      }
    } else if (element == Element.BASE) { // 只考虑第一个base标签，其中的href属性表示base路径（详见HTML base标签）
      if (base != null) { // We only consider the first occurrence of the Base element.
        String href = attributes.getValue("href");
        if (href != null) {
          base = href;
        }
      }
    } else if (element == Element.META) {
      String equiv = attributes.getValue("http-equiv");
      if (equiv == null) { // This condition covers several cases of XHTML meta
        equiv = attributes.getValue("name"); // 使用name属性（详见HTML meta标签）
      }

      String content = attributes.getValue("content");
      if (equiv != null && content != null) {
        equiv = equiv.toLowerCase();
        metaTags.put(equiv, content);

        // http-equiv="refresh" content="0;URL=http://foo.bar/..."
        if (equiv.equals("refresh") && (metaRefresh == null)) {	// refresh跳转
          int pos = content.toLowerCase().indexOf("url=");
          if (pos != -1) {
            metaRefresh = content.substring(pos + 4); // 跳转的目的地址
          }
          addToOutgoingUrls(metaRefresh, localName);
        }

        // http-equiv="location" content="http://foo.bar/..."
        if (equiv.equals("location") && (metaLocation == null)) { // location重定向
          metaLocation = content;	// 重定向目的地址
          //addToOutgoingUrls(metaRefresh, localName); // 原版本这里出错，github上已改正
          addToOutgoingUrls(metaLocation, localName);
        }
      }
    } else if (element == Element.BODY) { // body标签
      isWithinBodyElement = true;
    }
  }

  // 设置外链的tag,href，并加入到外链集合中
  private void addToOutgoingUrls(String href, String tag) {
    curUrl = new ExtractedUrlAnchorPair();
    curUrl.setHref(href);
    curUrl.setTag(tag);
    outgoingUrls.add(curUrl);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    Element element = HtmlFactory.getElement(localName);
    // 包含anchor的标签结束了
    if (element == Element.A || element == Element.AREA || element == Element.LINK) {
      anchorFlag = false;
      // 设置当前curUrl的锚文本内容
      if (curUrl != null) {
        String anchor = anchorText.toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
        if (!anchor.isEmpty()) {
          //超过最大长度限制时，进行截取
          if (anchor.length() > MAX_ANCHOR_LENGTH) {
            anchor = anchor.substring(0, MAX_ANCHOR_LENGTH) + "...";
          }
          curUrl.setTag(localName);
          curUrl.setAnchor(anchor);
        }
        // 清空，以供下次使用
        anchorText.delete(0, anchorText.length());
      }
      curUrl = null;
    } else if (element == Element.BODY) { // body标签结束
      isWithinBodyElement = false;
    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    if (isWithinBodyElement) {
		 if (bodyText.length() > 0) {
	         bodyText.append(' ');
	     }
    	// 记录下body中的内容
      bodyText.append(ch, start, length);
      
      //如果有锚文本，记录下来
      if (anchorFlag) {
        anchorText.append(new String(ch, start, length));
      }
    }
  }

  public String getBodyText() {
    return bodyText.toString();
  }

  public List<ExtractedUrlAnchorPair> getOutgoingUrls() {
    return outgoingUrls;
  }

  public String getBaseUrl() {
    return base;
  }

  public Map<String, String> getMetaTags() {
    return metaTags;
  }
}