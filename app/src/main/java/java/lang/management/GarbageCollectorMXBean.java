package java.lang.management;

/** Android no-op stub of the desktop-JVM garbage collector bean. */
public interface GarbageCollectorMXBean {

  String getName();

  long getCollectionTime();

  long getCollectionCount();
}
