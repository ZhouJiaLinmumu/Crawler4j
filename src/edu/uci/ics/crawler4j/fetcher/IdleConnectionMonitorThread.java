package edu.uci.ics.crawler4j.fetcher;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

// 监控http connection的空闲线程
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
          // Close expired connections，停止过期的连接
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
      notifyAll(); // 让run方法不再wait
    }
  }
}