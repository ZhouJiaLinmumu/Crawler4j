package edu.uci.ics.crawler4j.test;

import edu.uci.ics.crawler4j.url.UrlResolver;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Util;

public class Test {
	
	public static void testWebUrl() {
		WebURL url = new WebURL();
		url.setURL("http://www.baidu.com/index.html");
		System.out.println(url.getDomain());
		System.out.println(url.getSubDomain());
		System.out.println(url.getPath());
	}
	
	public static void testUrlResolver() {
		// �����·�������baseURL,ת��Ϊ����·��
		System.out.println(UrlResolver.resolveUrl("http://www.baidu.com/page/1.html", "../image/./2.jpg"));
	}
	
	public static void main(String[] args) {
		testWebUrl();
		testUrlResolver();
	}
}
