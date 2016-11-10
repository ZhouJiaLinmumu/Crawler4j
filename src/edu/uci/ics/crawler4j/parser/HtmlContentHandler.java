package edu.uci.ics.crawler4j.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ʹ��sax������
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlContentHandler extends DefaultHandler {

  private final int MAX_ANCHOR_LENGTH = 100;

  private enum Element {
    A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY
  }

  // ����string���͵ı�ǩ�������Ӧ��Element֮���ӳ���ϵ
  private static class HtmlFactory {
    private static Map<String, Element> name2Element;

    static {
      name2Element = new HashMap<>();
      for (Element element : Element.values()) {
    	//�����ַ�����enum������ӳ��
        name2Element.put(element.toString().toLowerCase(), element);
      }
    }

    public static Element getElement(String name) {
      return name2Element.get(name);
    }
  }

  private String base;	// ��ǰ��ҳ��base��ǩ��href��ֵ���ǵ�ǰҳ���basePath
  private String metaRefresh;	// meta��ǩ��http-equiv=refresh
  private String metaLocation;	// meta��ǩ��http-equiv="location"
  private Map<String, String> metaTags = new HashMap<>();	// ��ҳ��meta��ǩ��ֵ�ԣ������ж����

  private boolean isWithinBodyElement; // �ǲ�����body��ǩ֮��
  private StringBuilder bodyText;	// body��ǩ�ڰ������ı�����

  private List<ExtractedUrlAnchorPair> outgoingUrls; // ��������

  private ExtractedUrlAnchorPair curUrl = null;	// ��ǰ�����url�����ɵ�ExtractedUrlAnchorPair����
  private boolean anchorFlag = false;			// �Ƿ���anchor
  private StringBuilder anchorText = new StringBuilder();	// ê�ı�

  public HtmlContentHandler() {
    isWithinBodyElement = false;
    bodyText = new StringBuilder();
    outgoingUrls = new ArrayList<>();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    // �����ַ�������ȡ��Ӧ��element
	Element element = HtmlFactory.getElement(localName);

	// �����a,area,link��ǩ���õ���ǩ�е�href�����ӵ�ַ,�����뵽outgoingUrls�����С�������anchorFlagΪtrue����ʾ��ê�ı���
    // ��<a href="example.com">A sample anchor</a>�У�anchor='A sample anchor'��href=��example.com��,tag='a'
	if (element == Element.A || element == Element.AREA || element == Element.LINK) {
      String href = attributes.getValue("href");
      if (href != null) {
        anchorFlag = true;
        addToOutgoingUrls(href, localName);

      }
    } else if (element == Element.IMG) { 	// ���Ϊimg��ǩ�����ȡ���е�src�����뵽��������
      String imgSrc = attributes.getValue("src");
      if (imgSrc != null) {
        addToOutgoingUrls(imgSrc, localName);

      }
    } else if (element == Element.IFRAME || element == Element.FRAME || element == Element.EMBED) {
      String src = attributes.getValue("src");	// ���Ϊiframe,frame,embed��ǩ����ȡsrc�����뵽��������
      if (src != null) {
        addToOutgoingUrls(src, localName);

      }
    } else if (element == Element.BASE) { // ֻ���ǵ�һ��base��ǩ�����е�href���Ա�ʾbase·�������HTML base��ǩ��
      if (base != null) { // We only consider the first occurrence of the Base element.
        String href = attributes.getValue("href");
        if (href != null) {
          base = href;
        }
      }
    } else if (element == Element.META) {
      String equiv = attributes.getValue("http-equiv");
      if (equiv == null) { // This condition covers several cases of XHTML meta
        equiv = attributes.getValue("name"); // ʹ��name���ԣ����HTML meta��ǩ��
      }

      String content = attributes.getValue("content");
      if (equiv != null && content != null) {
        equiv = equiv.toLowerCase();
        metaTags.put(equiv, content);

        // http-equiv="refresh" content="0;URL=http://foo.bar/..."
        if (equiv.equals("refresh") && (metaRefresh == null)) {	// refresh��ת
          int pos = content.toLowerCase().indexOf("url=");
          if (pos != -1) {
            metaRefresh = content.substring(pos + 4); // ��ת��Ŀ�ĵ�ַ
          }
          addToOutgoingUrls(metaRefresh, localName);
        }

        // http-equiv="location" content="http://foo.bar/..."
        if (equiv.equals("location") && (metaLocation == null)) { // location�ض���
          metaLocation = content;	// �ض���Ŀ�ĵ�ַ
          //addToOutgoingUrls(metaRefresh, localName); // ԭ�汾�������github���Ѹ���
          addToOutgoingUrls(metaLocation, localName);
        }
      }
    } else if (element == Element.BODY) { // body��ǩ
      isWithinBodyElement = true;
    }
  }

  // ����������tag,href�������뵽����������
  private void addToOutgoingUrls(String href, String tag) {
    curUrl = new ExtractedUrlAnchorPair();
    curUrl.setHref(href);
    curUrl.setTag(tag);
    outgoingUrls.add(curUrl);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    Element element = HtmlFactory.getElement(localName);
    // ����anchor�ı�ǩ������
    if (element == Element.A || element == Element.AREA || element == Element.LINK) {
      anchorFlag = false;
      // ���õ�ǰcurUrl��ê�ı�����
      if (curUrl != null) {
        String anchor = anchorText.toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
        if (!anchor.isEmpty()) {
          //������󳤶�����ʱ�����н�ȡ
          if (anchor.length() > MAX_ANCHOR_LENGTH) {
            anchor = anchor.substring(0, MAX_ANCHOR_LENGTH) + "...";
          }
          curUrl.setTag(localName);
          curUrl.setAnchor(anchor);
        }
        // ��գ��Թ��´�ʹ��
        anchorText.delete(0, anchorText.length());
      }
      curUrl = null;
    } else if (element == Element.BODY) { // body��ǩ����
      isWithinBodyElement = false;
    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    if (isWithinBodyElement) {
		 if (bodyText.length() > 0) {
	         bodyText.append(' ');
	     }
    	// ��¼��body�е�����
      bodyText.append(ch, start, length);
      
      //�����ê�ı�����¼����
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