package com.zaxxer.hikari.util;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class UtilityElf {

    private UtilityElf() {
        super();
    }

    public static String getNullIfEmpty(String text) {
        return text == null ? null : (text.trim().isEmpty() ? null : text.trim());
    }

    public static void quietlySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
        }

    }

    public static boolean safeIsAssignableFrom(Object obj, String className) {
        try {
            Class<?> clazz = Class.forName(className);

            return clazz.isAssignableFrom(obj.getClass());
        } catch (ClassNotFoundException classnotfoundexception) {
            return false;
        }
    }

    public static <T> T createInstance(String className, Class<T> clazz, Object... args) {
        if (className == null) {
            return null;
        } else {
            try {
                Class<?> loaded = UtilityElf.class.getClassLoader().loadClass(className);

                if (args.length == 0) {
                    return (T) clazz.cast(loaded.getDeclaredConstructor().newInstance());
                } else {
                    Class<?>[] argClasses = new Class[args.length];

                    for (int i = 0; i < args.length; ++i) {
                        argClasses[i] = args[i].getClass();
                    }

                    Constructor<?> constructor = loaded.getConstructor(argClasses);

                    return (T) clazz.cast(constructor.newInstance(args));
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int queueSize, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
        return createThreadPoolExecutor(new LinkedBlockingQueue(queueSize), threadName, threadFactory, policy);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(BlockingQueue<Runnable> queue, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
        if (threadFactory == null) {
            threadFactory = new UtilityElf.DefaultThreadFactory(threadName);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, queue, threadFactory, policy);

        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static int getTransactionIsolation(String transactionIsolationName) {
        if (transactionIsolationName != null) {
            try {
                String upperCaseIsolationLevelName = transactionIsolationName.toUpperCase(Locale.ENGLISH);

                return IsolationLevel.valueOf(upperCaseIsolationLevelName).getLevelId();
            } catch (IllegalArgumentException illegalargumentexception) {
                try {
                    int level = Integer.parseInt(transactionIsolationName);

                    for (IsolationLevel iso : IsolationLevel.values()) {
                        if (iso.getLevelId() == level) {
                            return iso.getLevelId();
                        }
                    }

                    throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName);
                } catch (NumberFormatException numberformatexception) {
                    throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName, numberformatexception);
                }
            }
        } else {
            return -1;
        }
    }

    public static class CustomDiscardPolicy implements RejectedExecutionHandler {

        public CustomDiscardPolicy() {
            super();
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {}
    }

    public static final class DefaultThreadFactory implements ThreadFactory {

        private final String threadName;
        private final boolean daemon;

        public DefaultThreadFactory(String threadName) {
            super();
            this.threadName = threadName;
            this.daemon = true;
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, this.threadName);

            thread.setDaemon(this.daemon);
            return thread;
        }
    }
}
