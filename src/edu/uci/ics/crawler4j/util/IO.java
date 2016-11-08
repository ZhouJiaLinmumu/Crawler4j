package edu.uci.ics.crawler4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;


public class IO {
  
  //日志记录对象	
  private static Logger logger = LoggerFactory.getLogger(IO.class);

  public static boolean deleteFolder(File folder) {
	// 删除目录，并判断执行删除操作后目录是否存在
    return deleteFolderContents(folder) && folder.delete();
  }

  // 使用递归的方法删除一个目录及其下的所有子目录及文件
  public static boolean deleteFolderContents(File folder) {
    logger.debug("Deleting content of: " + folder.getAbsolutePath());
    File[] files = folder.listFiles();
    for (File file : files) {
      // 如果是文件，则直接删除	
      if (file.isFile()) {
        if (!file.delete()) {
          return false;
        }
      } else {	// 如果是目录，则对目录递归执行deleteFolderContents操作
        if (!deleteFolder(file)) {
          return false;
        }
      }
    }
    // 删除完毕，返回true
    return true;
  }
}