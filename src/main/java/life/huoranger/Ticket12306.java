package life.huoranger;

import life.huoranger.constant.ZookeeperConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author: 清风徐来
 * @date: 12/5/2022 2:14 AM
 * @description:
 */
public class Ticket12306 implements Runnable{

    private int tickets = 10;

    private InterProcessMutex lock;

    public Ticket12306() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConstant.server)
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .namespace("huoranger").build();
        client.start();
        this.lock = new InterProcessMutex(client, "/lock");
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 获取锁
                lock.acquire(3, TimeUnit.SECONDS);
                if (tickets > 0) {
                    System.out.println(Thread.currentThread().getName() + ":" + tickets);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // 释放锁
                    lock.release();
                } catch (Exception ignored) {

                }
            }
        }
    }
}
