package java.lang.management;

/** Android no-op stub of the desktop-JVM thread info snapshot. */
public class ThreadInfo {

  public String getThreadName() {
    return null;
  }

  public Thread.State getThreadState() {
    return Thread.State.RUNNABLE;
  }

  public StackTraceElement[] getStackTrace() {
    return new StackTraceElement[0];
  }

  public boolean isSuspended() {
    return false;
  }

  public boolean isInNative() {
    return false;
  }

  public String getLockName() {
    return null;
  }

  public long getLockOwnerId() {
    return -1L;
  }

  public String getLockOwnerName() {
    return null;
  }
}
