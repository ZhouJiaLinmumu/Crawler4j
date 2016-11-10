package edu.uci.ics.crawler4j.fetcher;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

// ���http connection�Ŀ����߳�
public class IdleConnectionMonitorThread extends Thread {

  // PoolingHttpClientConnectionManager is a more complex implementation that manages a pool
  // of client connections and is able to service connection requests from multiple execution threads.
  private final PoolingHttpClientConnectionManager connMgr;
  private volatile boolean shutdown;

  public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
    super("Connection Manager");
    this.connMgr = connMgr;
  }

  @Override
  public void run() {
    try {
      while (!shutdown) {
        synchronized (this) {
          wait(5000);
          // Close expired connections��ֹͣ���ڵ�����
          connMgr.closeExpiredConnections();
          // Optionally, close connections that have been idle longer than 30 sec
          connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
        }
      }
    } catch (InterruptedException ex) {
      // terminate
    }
  }

  public void shutdown() {
    shutdown = true;
    synchronized (this) {
      notifyAll(); // ��run��������wait
    }
  }
}