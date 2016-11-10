package edu.uci.ics.crawler4j.robotstxt;

// ��ŵ�ǰHost��robot.txtָ��
public class HostDirectives {

  // If we fetched the directives for this host more than
  // 24 hours, we have to re-fetch it.
  private static final long EXPIRATION_DELAY = 24 * 60 * 1000L;

  // ��������ʵĹ���
  private RuleSet disallows = new RuleSet();
  // ������ʵĹ���
  private RuleSet allows = new RuleSet();

  // ��ȡrobotָ���ʱ��
  private long timeFetched;
  // ���һ��ʹ��robotָ��ϵ�ʱ��
  private long timeLastAccessed;

  public HostDirectives() {
    timeFetched = System.currentTimeMillis();
  }

  // �������ָ��ʱ�����ƣ���Ҫ���»�ȡrobotָ��
  public boolean needsRefetch() {
    return (System.currentTimeMillis() - timeFetched > EXPIRATION_DELAY);
  }

  // ��ǰpath�Ƿ��������
  public boolean allows(String path) {
    timeLastAccessed = System.currentTimeMillis();
    // disallows������û�л���allows�������У�����Է���
    return !disallows.containsPrefixOf(path) || allows.containsPrefixOf(path);
  }

  // ���ӵ�ǰpath����������ʵļ�����ȥ
  public void addDisallow(String path) {
    disallows.add(path);
  }

  // ���ӵ�ǰpath��������ʵļ�����ȥ
  public void addAllow(String path) {
    allows.add(path);
  }

  // ���һ�η���ʱ��
  public long getLastAccessTime() {
    return timeLastAccessed;
  }
}