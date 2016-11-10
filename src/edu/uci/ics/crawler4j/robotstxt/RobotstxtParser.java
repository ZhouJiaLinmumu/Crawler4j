package edu.uci.ics.crawler4j.robotstxt;

import java.util.StringTokenizer;

// 根据网站的robot.txt文本，构建allows和disallow集合
public class RobotstxtParser {

  // 当使用String.matches方法调用时，"?i"表示忽略大小写	
  private static final String PATTERNS_USERAGENT = "(?i)^User-agent:.*";
  private static final String PATTERNS_DISALLOW = "(?i)Disallow:.*";
  private static final String PATTERNS_ALLOW = "(?i)Allow:.*";

  // "User-agent:"长度为11
  private static final int PATTERNS_USERAGENT_LENGTH = 11;
  private static final int PATTERNS_DISALLOW_LENGTH = 9;
  private static final int PATTERNS_ALLOW_LENGTH = 6;

  public static HostDirectives parse(String content, String myUserAgent) {

    HostDirectives directives = null;
    boolean inMatchingUserAgent = false;

    // 一次提取robot.txt的每一行
    StringTokenizer st = new StringTokenizer(content, "\n\r");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      
      // #号之后的都是注释
      int commentIndex = line.indexOf("#");
      if (commentIndex > -1) {
        line = line.substring(0, commentIndex);
      }

      // remove any html markup
      line = line.replaceAll("<[^>]+>", ""); 	// "<[除了右括号的字符]+>"
      line = line.trim();

      if (line.length() == 0) {
        continue;
      }

      if (line.matches(PATTERNS_USERAGENT)) {	// User-agenet行的内容
        String ua = line.substring(PATTERNS_USERAGENT_LENGTH).trim().toLowerCase();
        // user-agent是否是针对当前爬虫的
        if (ua.equals("*") || ua.contains(myUserAgent)) {
          inMatchingUserAgent = true;
        } else {
          inMatchingUserAgent = false;
        }
      } else if (line.matches(PATTERNS_DISALLOW)) {	// disallow行的内容
        if (!inMatchingUserAgent) {
          continue;
        }
        String path = line.substring(PATTERNS_DISALLOW_LENGTH).trim();
        if (path.endsWith("*")) {
        	// 获取星号之前的path路径
        	path = path.substring(0, path.length() - 1);
        }
        path = path.trim();
        if (path.length() > 0) {
          if (directives == null) {
            directives = new HostDirectives();
          }
          // 增加disallow规则
          directives.addDisallow(path);
        }
      } else if (line.matches(PATTERNS_ALLOW)) {	// allow行的内容
        if (!inMatchingUserAgent) {
          continue;
        }
        String path = line.substring(PATTERNS_ALLOW_LENGTH).trim();
        // 获取星号之前的Path路径
        if (path.endsWith("*")) {
          path = path.substring(0, path.length() - 1);
        }
        path = path.trim();
        if (directives == null) {
          directives = new HostDirectives();
        }
        // 增加allow规则
        directives.addAllow(path);
      }
    }

    return directives;
  }
}