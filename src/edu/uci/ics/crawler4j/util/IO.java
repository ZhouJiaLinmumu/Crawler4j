package edu.uci.ics.crawler4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;


public class IO {
  
  //��־��¼����	
  private static Logger logger = LoggerFactory.getLogger(IO.class);

  public static boolean deleteFolder(File folder) {
	// ɾ��Ŀ¼�����ж�ִ��ɾ��������Ŀ¼�Ƿ����
    return deleteFolderContents(folder) && folder.delete();
  }

  // ʹ�õݹ�ķ���ɾ��һ��Ŀ¼�����µ�������Ŀ¼���ļ�
  public static boolean deleteFolderContents(File folder) {
    logger.debug("Deleting content of: " + folder.getAbsolutePath());
    File[] files = folder.listFiles();
    for (File file : files) {
      // ������ļ�����ֱ��ɾ��	
      if (file.isFile()) {
        if (!file.delete()) {
          return false;
        }
      } else {	// �����Ŀ¼�����Ŀ¼�ݹ�ִ��deleteFolderContents����
        if (!deleteFolder(file)) {
          return false;
        }
      }
    }
    // ɾ����ϣ�����true
    return true;
  }
}