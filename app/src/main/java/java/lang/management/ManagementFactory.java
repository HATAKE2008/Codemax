package java.lang.management;

/**
 * Minimal Android replacement for the desktop-JVM {@code java.lang.management} API.
 *
 * <p>The bundled Kotlin 2.3 compiler ({@code kotlin-compiler-embeddable}) unconditionally calls
 * {@code ManagementFactory.getThreadMXBean()} from {@code PerformanceManager}, but the {@code
 * java.lang.management} package does not exist on ART. These no-op stubs are dexed into the app so
 * that class resolution succeeds; all reported values are zero/empty.
 */
public final class ManagementFactory {

  private static final ThreadMXBean THREAD_MX_BEAN = new AndroidThreadMXBean();

  private static final CompilationMXBean COMPILATION_MX_BEAN =
      new CompilationMXBean() {
        @Override
        public long getTotalCompilationTime() {
          return 0L;
        }
      };

  private static final MemoryMXBean MEMORY_MX_BEAN = new MemoryMXBean() {};

  private ManagementFactory() {}

  public static ThreadMXBean getThreadMXBean() {
    return THREAD_MX_BEAN;
  }

  public static CompilationMXBean getCompilationMXBean() {
    return COMPILATION_MX_BEAN;
  }

  public static MemoryMXBean getMemoryMXBean() {
    return MEMORY_MX_BEAN;
  }

  @SuppressWarnings("unchecked")
  public static java.util.List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
    return java.util.Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  public static java.util.List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
    return java.util.Collections.emptyList();
  }
}
