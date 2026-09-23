package java.lang.management;

/** Android no-op stub of the desktop-JVM memory pool bean. */
public interface MemoryPoolMXBean {

  MemoryType getType();

  MemoryUsage getUsage();

  boolean isUsageThresholdSupported();

  void setUsageThreshold(long threshold);

  boolean isCollectionUsageThresholdSupported();

  void setCollectionUsageThreshold(long threshold);
}
