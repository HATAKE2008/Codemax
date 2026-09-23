package java.lang.management;

/** Backing implementation for {@link ManagementFactory#getThreadMXBean()} on Android. */
class AndroidThreadMXBean implements ThreadMXBean {

  @Override
  public long getCurrentThreadCpuTime() {
    return 0L;
  }

  @Override
  public long getCurrentThreadUserTime() {
    return 0L;
  }

  @Override
  public void setThreadCpuTimeEnabled(boolean enable) {}

  @Override
  public boolean isThreadCpuTimeEnabled() {
    return false;
  }

  @Override
  public long[] getAllThreadIds() {
    return new long[0];
  }

  @Override
  public ThreadInfo[] getThreadInfo(long[] ids, int maxDepth) {
    return new ThreadInfo[0];
  }

  @Override
  public ThreadInfo[] dumpAllThreads(boolean lockedMonitors, boolean lockedSynchronizers) {
    return new ThreadInfo[0];
  }
}
