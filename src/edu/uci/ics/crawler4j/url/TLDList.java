package edu.uci.ics.crawler4j.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

// ������򱾵��ļ��л�ȡ�����������б�
public class TLDList {

  private final static String TLD_NAMES_ONLINE_URL = "https://publicsuffix.org/list/effective_tld_names.dat";
  private final static String TLD_NAMES_TXT_FILENAME = "/tld-names.txt";
  private final static Logger logger = LoggerFactory.getLogger(TLDList.class);

  private Set<String> tldSet = new HashSet<>(10000);

  private static TLDList instance = new TLDList(); // Singleton

  private TLDList() {
    try {
      URL url = new URL(TLD_NAMES_ONLINE_URL);
      // �������ϻ�ȡTLD�����б��ļ�
      try (InputStream stream = url.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
        logger.debug("Fetching the most updated TLD list online");
        
        String line;
        // ���ζ�ȡ����������
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty() || line.startsWith("//")) {
            continue;
          }
          tldSet.add(line);
        }
      } catch (Exception ex) {
        throw new Exception("Error while retrieving online TLD List");
      }
    } catch (Exception ex) { // Reverting to offline TLD List
      logger.warn("Couldn't fetch the online list of TLDs from: {}", TLD_NAMES_ONLINE_URL);
      logger.info("Fetching the list from my local file {}", TLD_NAMES_TXT_FILENAME);

      // �ӱ����ļ��ж�ȡTLD�б�
      try (InputStream stream = this.getClass().getResourceAsStream(TLD_NAMES_TXT_FILENAME);
           BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty() || line.startsWith("//")) {
            continue;
          }
          tldSet.add(line);
        }
      } catch (Exception ex2) {
        logger.error("Couldn't find " + TLD_NAMES_TXT_FILENAME, ex2);
        logger.error("No TLD List exiting...");
        System.exit(-1);
      }
    }
  }

  public static TLDList getInstance() {
    return instance;
  }

  // �ж�ĳ�������Ƿ�����ڶ���������
  public boolean contains(String str) {
    return tldSet.contains(str);
  }
}