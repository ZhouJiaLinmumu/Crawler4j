package edu.uci.ics.crawler4j.parser;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Set;

//����ӿ���
public interface ParseData {

  //�õ���ǰҳ�����������
  Set<WebURL> getOutgoingUrls();

  void setOutgoingUrls(Set<WebURL> outgoingUrls);

  @Override
  String toString();
}