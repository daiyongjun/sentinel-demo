package com.weiyan.example.file.rule.flow;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.util.TimeUtil;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流量规则的定义
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/9/12 11:16
 */
public class FlowQpsDemo {
    private static final String RESOURCE_NAME = "abc";
    private static volatile boolean stop = false;
    private static AtomicInteger pass = new AtomicInteger();
    private static AtomicInteger block = new AtomicInteger();
    private static AtomicInteger total = new AtomicInteger();
    private static final int THREAD_COUNT = 10;
    private static int seconds = 660 + 40;

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        tick();
        initFlowQpsRule();
        timerTask();
    }

    /**
     * 定义qps的流量规则
     */
    private static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule(RESOURCE_NAME);
        // set limit qps to 20
        rule.setCount(1);
//        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 创建单独线程进行计数器计算
     */
    private static void tick() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("tick-pool-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(100), threadFactory);
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
                System.out.println(Thread.currentThread().getName()+", " + seconds + ", " + TimeUtil.currentTimeMillis() + ", total:"
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
    private static void timerTask() {
//        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("tick-pool-%d").daemon(true).build();
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("task-pool-%d").daemon(true).build();
//        ExecutorService executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(1024),threadFactory);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT,threadFactory);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Runnable runnable = () -> {
                while (true) {
                    Entry entry = null;
                    try {
                        entry = SphU.entry(RESOURCE_NAME, EntryType.IN);
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