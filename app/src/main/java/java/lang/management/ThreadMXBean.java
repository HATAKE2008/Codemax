package java.lang.management;

/** Android no-op stub of the desktop-JVM thread metrics bean. */
public interface ThreadMXBean {

  long getCurrentThreadCpuTime();

  long getCurrentThreadUserTime();

  void setThreadCpuTimeEnabled(boolean enable);

  boolean isThreadCpuTimeEnabled();

  long[] getAllThreadIds();

  ThreadInfo[] getThreadInfo(long[] ids, int maxDepth);

  ThreadInfo[] dumpAllThreads(boolean lockedMonitors, boolean lockedSynchronizers);
}
