package edu.uci.ics.crawler4j.robotstxt;

import java.util.StringTokenizer;

// ������վ��robot.txt�ı�������allows��disallow����
public class RobotstxtParser {

  // ��ʹ��String.matches��������ʱ��"?i"��ʾ���Դ�Сд	
  private static final String PATTERNS_USERAGENT = "(?i)^User-agent:.*";
  private static final String PATTERNS_DISALLOW = "(?i)Disallow:.*";
  private static final String PATTERNS_ALLOW = "(?i)Allow:.*";

  // "User-agent:"����Ϊ11
  private static final int PATTERNS_USERAGENT_LENGTH = 11;
  private static final int PATTERNS_DISALLOW_LENGTH = 9;
  private static final int PATTERNS_ALLOW_LENGTH = 6;

  public static HostDirectives parse(String content, String myUserAgent) {

    HostDirectives directives = null;
    boolean inMatchingUserAgent = false;

    // һ����ȡrobot.txt��ÿһ��
    StringTokenizer st = new StringTokenizer(content, "\n\r");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      
      // #��֮��Ķ���ע��
      int commentIndex = line.indexOf("#");
      if (commentIndex > -1) {
        line = line.substring(0, commentIndex);
      }

      // remove any html markup
      line = line.replaceAll("<[^>]+>", ""); 	// "<[���������ŵ��ַ�]+>"
      line = line.trim();

      if (line.length() == 0) {
        continue;
      }

      if (line.matches(PATTERNS_USERAGENT)) {	// User-agenet�е�����
        String ua = line.substring(PATTERNS_USERAGENT_LENGTH).trim().toLowerCase();
        // user-agent�Ƿ�����Ե�ǰ�����
        if (ua.equals("*") || ua.contains(myUserAgent)) {
          inMatchingUserAgent = true;
        } else {
          inMatchingUserAgent = false;
        }
      } else if (line.matches(PATTERNS_DISALLOW)) {	// disallow�е�����
        if (!inMatchingUserAgent) {
          continue;
        }
        String path = line.substring(PATTERNS_DISALLOW_LENGTH).trim();
        if (path.endsWith("*")) {
        	// ��ȡ�Ǻ�֮ǰ��path·��
        	path = path.substring(0, path.length() - 1);
        }
        path = path.trim();
        if (path.length() > 0) {
          if (directives == null) {
            directives = new HostDirectives();
          }
          // ����disallow����
          directives.addDisallow(path);
        }
      } else if (line.matches(PATTERNS_ALLOW)) {	// allow�е�����
        if (!inMatchingUserAgent) {
          continue;
        }
        String path = line.substring(PATTERNS_ALLOW_LENGTH).trim();
        // ��ȡ�Ǻ�֮ǰ��Path·��
        if (path.endsWith("*")) {
          path = path.substring(0, path.length() - 1);
        }
        path = path.trim();
        if (directives == null) {
          directives = new HostDirectives();
        }
        // ����allow����
        directives.addAllow(path);
      }
    }

    return directives;
  }
}