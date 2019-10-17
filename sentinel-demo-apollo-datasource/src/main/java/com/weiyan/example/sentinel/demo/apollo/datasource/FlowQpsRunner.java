package com.weiyan.example.sentinel.demo.apollo.datasource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.TimeUtil;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Flow QPS runner.
 *
 * @author Carpenter Lee
 * @author Eric Zhao
 */
class FlowQpsRunner {

    private final String resourceName;
    private final int threadCount;
    private int seconds;
    private final AtomicInteger pass = new AtomicInteger();
    private final AtomicInteger block = new AtomicInteger();
    private final AtomicInteger total = new AtomicInteger();

    private volatile boolean stop = false;

    FlowQpsRunner(String resourceName, int threadCount, int seconds) {
        this.resourceName = resourceName;
        this.threadCount = threadCount;
        this.seconds = seconds;
    }

    /**
     * 创建单独线程进行计数器计算
     */
    void tick() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("tick-pool-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(100), threadFactory);
        //匿名实例化
        Runnable runnable = () -> {
            System.out.println("begin to statistic!!!");
            long oldTotal = 0;
            long oldPass = 0;
            long oldBlock = 0;
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                long globalTotal = total.get();
                long oneSecondTotal = globalTotal - oldTotal;
                oldTotal = globalTotal;

                long globalPass = pass.get();
                long oneSecondPass = globalPass - oldPass;
                oldPass = globalPass;

                long globalBlock = block.get();
                long oneSecondBlock = globalBlock - oldBlock;
                oldBlock = globalBlock;
                System.out.println(Thread.currentThread().getName() + ", " + seconds + ", " + TimeUtil.currentTimeMillis() + ", total:"
                        + oneSecondTotal + ", pass:"
                        + oneSecondPass + ", block:" + oneSecondBlock);
                if (seconds-- <= 0) {
                    stop = true;
                }
            }
            System.exit(0);
        };
        executor.execute(runnable);
    }

    /**
     * 构建多任务处理
     */
    void timerTask() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("task-pool-%d").daemon(true).build();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, threadFactory);
        for (int i = 0; i < threadCount; i++) {
            Runnable runnable = () -> {
                while (true) {
                    Entry entry = null;
                    try {
                        entry = SphU.entry(resourceName, EntryType.IN);
                        pass.incrementAndGet();
                        try {
                            TimeUnit.MILLISECONDS.sleep(20);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    } catch (BlockException e1) {
                        block.incrementAndGet();
                        try {
                            TimeUnit.MILLISECONDS.sleep(20);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    } catch (Exception e2) {
                        // biz exception
                    } finally {
                        total.incrementAndGet();
                        if (entry != null) {
                            entry.exit();
                        }
                    }
                }
            };
            executor.execute(runnable);
        }
    }
}