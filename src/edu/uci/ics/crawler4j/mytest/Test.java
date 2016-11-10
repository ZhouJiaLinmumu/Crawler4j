package edu.uci.ics.crawler4j.mytest;


import edu.uci.ics.crawler4j.url.UrlResolver;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Util;

public class Test {
	
	public static void testUtil() {
//		System.out.println(Util.byteArray2Int(Util.int2ByteArray(100)));
//		System.out.println(Util.byteArray2Int(Util.int2ByteArray(-100)));
//		System.out.println(Util.byteArray2Long(Util.long2ByteArray(100)));
		System.out.println(Util.byteArray2Long(Util.long2ByteArray(-100L)));	
		
	}
	
	public static void testUrlResolver() {
		// 将相对路径，结合baseURL,转换为绝对路径
		System.out.println(UrlResolver.resolveUrl("http://www.baidu.com/page/1.html", "../image/./2.jpg"));
	}	
	
	public static void testWebUrl() {
		WebURL url = new WebURL();
		url.setURL("http://www.baidu.com/index.html");
		System.out.println(url.getDomain());
		System.out.println(url.getSubDomain());
		System.out.println(url.getPath());
		System.out.println(url.getURL());
	}
	
	public static void main(String[] args) {
		testWebUrl();
	}
}

