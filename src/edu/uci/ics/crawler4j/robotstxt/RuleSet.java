package edu.uci.ics.crawler4j.robotstxt;

import java.util.SortedSet;
import java.util.TreeSet;

// RuleSet类根据robot.txt来定义爬虫爬取url时的rule
public class RuleSet extends TreeSet<String> {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean add(String str) {
	// 返回所有小于str的String集合
    SortedSet<String> sub = headSet(str);
    // 例如如果已经加入了http://www.baidu.com/image，则不用加入http://www.baidu.con/image/1.jpg
    if (!sub.isEmpty() && str.startsWith(sub.last())) {
      // no need to add; prefix is already present
      return false;
    }
    boolean retVal = super.add(str);
    // 返回所有大于等于str+'\0'的String的集合
    sub = tailSet(str + "\0");
    // 去掉冗余的规则，例如如果加入的是http://www.baidu.com/image，则需要去掉http://www.baidu.com/image/1.jpg
    while (!sub.isEmpty() && sub.first().startsWith(str)) {
      // remove redundant entries
      sub.remove(sub.first());
    }
    return retVal;
  }

  // 是否包含s作为前缀的规则
  public boolean containsPrefixOf(String s) {
    SortedSet<String> sub = headSet(s);
    // because redundant prefixes have been eliminated,
    // only a test against last item in headSet is necessary
    if (!sub.isEmpty() && s.startsWith(sub.last())) {	// 如果包含有大的规则，则等价于当前规则已经包含
      return true; // prefix substring exists
    }
    // might still exist exactly (headSet does not contain boundary)
    return contains(s);
  }
}