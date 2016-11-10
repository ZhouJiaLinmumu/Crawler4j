package edu.uci.ics.crawler4j.url;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity	// Berkley DB Annotation
public class WebURL implements Serializable {

  private static final long serialVersionUID = 1L;

  @PrimaryKey
  private String url;		// 当前页面的url

  private int docid;		// 为当前网页分配的一个docId
  private int parentDocid;	// 在网页a的页面上找到指向b的链接，则a是b的parentDocid
  private String parentUrl;	// 在网页a的页面上找到指向b的链接，则a是b的parentUrl
  private short depth;		// 爬取深度， 从0开始计数
  private String domain;	// 当前网页的主域名
  private String subDomain;	// 当前网页的子域名
  private String path;		// 当前网页在网站中的资源路径
  private String anchor;	// 超链接标签中的文本
  private byte priority;	// 爬取的优先级，越低代表优先级越高
  private String tag;		// 标签


  /**
   * @return unique document id assigned to this Url.
   */
  public int getDocid() {
    return docid;
  }

  public void setDocid(int docid) {
    this.docid = docid;
  }

  /**
   * @return Url string
   */
  public String getURL() {
    return url;
  }

  public void setURL(String url) {
    this.url = url;

    // 从"http://"开始作为domain的起点
    int domainStartIdx = url.indexOf("//") + 2;	
    // 第一个斜杠作为domain的终点，例如”http://www.baidu.com/“
    int domainEndIdx = url.indexOf('/', domainStartIdx);
    // 有点没有斜杠，如http://www.baidu.com
    domainEndIdx = domainEndIdx > domainStartIdx ? domainEndIdx : url.length();
    domain = url.substring(domainStartIdx, domainEndIdx);
    subDomain = "";
    //根据点进行拆分
    String[] parts = domain.split("\\.");	
    if (parts.length > 2) {
      // 默认的domain包含两个字段，如www.baidu.com中的baidu.com
      domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
      int limit = 2;
      // 有的包含3个字段，如www.sina.com.cn中的sina.com.cn
      if (TLDList.getInstance().contains(domain)) {
        domain = parts[parts.length - 3] + "." + domain;
        limit = 3;
      }
      for (int i = 0; i < parts.length - limit; i++) {
    	// 加上分隔符
        if (subDomain.length() > 0) {
          subDomain += ".";
        }
        subDomain += parts[i];
      }
    }
    path = url.substring(domainEndIdx);
    // 如果url中带有参数（即含有?），则?之后的不是path
    int pathEndIdx = path.indexOf('?');
    if (pathEndIdx >= 0) {
      path = path.substring(0, pathEndIdx);
    }
  }

  /**
   * @return
   *      unique document id of the parent page. The parent page is the
   *      page in which the Url of this page is first observed.
   */
  public int getParentDocid() {
    return parentDocid;
  }

  public void setParentDocid(int parentDocid) {
    this.parentDocid = parentDocid;
  }

  /**
   * @return
   *      url of the parent page. The parent page is the page in which
   *      the Url of this page is first observed.
   */
  public String getParentUrl() {
    return parentUrl;
  }

  public void setParentUrl(String parentUrl) {
    this.parentUrl = parentUrl;
  }

  /**
   * @return
   *      crawl depth at which this Url is first observed. Seed Urls
   *      are at depth 0. Urls that are extracted from seed Urls are at depth 1, etc.
   */
  public short getDepth() {
    return depth;
  }

  public void setDepth(short depth) {
    this.depth = depth;
  }

  /**
   * @return
   *      domain of this Url. For 'http://www.example.com/sample.htm', domain will be 'example.com'
   */
  public String getDomain() {
    return domain;
  }

  public String getSubDomain() {
    return subDomain;
  }

  /**
   * @return
   *      path of this Url. For 'http://www.example.com/sample.htm', path will be 'sample.htm'
   */
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return
   *      anchor string. For example, in <a href="example.com">A sample anchor</a>
   *      the anchor string is 'A sample anchor'
   */
  public String getAnchor() {
    return anchor;
  }

  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }

  /**
   * @return priority for crawling this URL. A lower number results in higher priority.
   */
  public byte getPriority() {
    return priority;
  }

  public void setPriority(byte priority) {
    this.priority = priority;
  }

  /**
   * @return tag in which this URL is found, like 'a' , 'href' ,····
   * */
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public int hashCode() {
    return url.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WebURL otherUrl = (WebURL) o;
    return url != null && url.equals(otherUrl.getURL());

  }

  @Override
  public String toString() {
    return url;
  }
}