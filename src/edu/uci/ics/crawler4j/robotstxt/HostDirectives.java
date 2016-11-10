package edu.uci.ics.crawler4j.robotstxt;

// 存放当前Host的robot.txt指令
public class HostDirectives {

  // If we fetched the directives for this host more than
  // 24 hours, we have to re-fetch it.
  private static final long EXPIRATION_DELAY = 24 * 60 * 1000L;

  // 不允许访问的规则
  private RuleSet disallows = new RuleSet();
  // 允许访问的规则
  private RuleSet allows = new RuleSet();

  // 获取robot指令的时间
  private long timeFetched;
  // 最后一次使用robot指令集合的时间
  private long timeLastAccessed;

  public HostDirectives() {
    timeFetched = System.currentTimeMillis();
  }

  // 如果超出指定时间限制，需要重新获取robot指令
  public boolean needsRefetch() {
    return (System.currentTimeMillis() - timeFetched > EXPIRATION_DELAY);
  }

  // 当前path是否允许访问
  public boolean allows(String path) {
    timeLastAccessed = System.currentTimeMillis();
    // disallows集合中没有或者allows集合中有，则可以访问
    return !disallows.containsPrefixOf(path) || allows.containsPrefixOf(path);
  }

  // 增加当前path到不允许访问的集合中去
  public void addDisallow(String path) {
    disallows.add(path);
  }

  // 增加当前path到允许访问的集合中去
  public void addAllow(String path) {
    allows.add(path);
  }

  // 最后一次访问时间
  public long getLastAccessTime() {
    return timeLastAccessed;
  }
}