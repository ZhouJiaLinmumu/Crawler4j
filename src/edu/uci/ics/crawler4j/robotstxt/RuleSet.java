package edu.uci.ics.crawler4j.robotstxt;

import java.util.SortedSet;
import java.util.TreeSet;

// RuleSet�����robot.txt������������ȡurlʱ��rule
public class RuleSet extends TreeSet<String> {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean add(String str) {
	// ��������С��str��String����
    SortedSet<String> sub = headSet(str);
    // ��������Ѿ�������http://www.baidu.com/image�����ü���http://www.baidu.con/image/1.jpg
    if (!sub.isEmpty() && str.startsWith(sub.last())) {
      // no need to add; prefix is already present
      return false;
    }
    boolean retVal = super.add(str);
    // �������д��ڵ���str+'\0'��String�ļ���
    sub = tailSet(str + "\0");
    // ȥ������Ĺ�����������������http://www.baidu.com/image������Ҫȥ��http://www.baidu.com/image/1.jpg
    while (!sub.isEmpty() && sub.first().startsWith(str)) {
      // remove redundant entries
      sub.remove(sub.first());
    }
    return retVal;
  }

  // �Ƿ����s��Ϊǰ׺�Ĺ���
  public boolean containsPrefixOf(String s) {
    SortedSet<String> sub = headSet(s);
    // because redundant prefixes have been eliminated,
    // only a test against last item in headSet is necessary
    if (!sub.isEmpty() && s.startsWith(sub.last())) {	// ��������д�Ĺ�����ȼ��ڵ�ǰ�����Ѿ�����
      return true; // prefix substring exists
    }
    // might still exist exactly (headSet does not contain boundary)
    return contains(s);
  }
}