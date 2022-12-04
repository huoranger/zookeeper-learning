package life.huoranger;

import life.huoranger.constant.ZookeeperConstant;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: 清风徐来
 * @date: 12/4/2022 7:32 PM
 * @description:
 */
public class CuratorWatcheTest {

    private CuratorFramework client;


    /**
     * 建立链接
     */
    @Before
    public void testCreateConnection() {
        RetryPolicy retryPolice = new ExponentialBackoffRetry(3000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConstant.server)
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolice).namespace("huoranger").build();
        client.start();
    }


    /**
     * 释放链接
     */
    @After
    public void testReleaseConnection() {
        if (client != null) {
            client.close();
        }
    }


    /**
     * 监听节点
     */
    @Test
    public void testNodeCache() throws Exception {
        NodeCache cache = new NodeCache(client, "/app1");
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了~");
                // 获取修改节点后的数据
                byte[] data = cache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });
        // 如果设置为true，则开启监听时，加载缓冲数据
        cache.start(true);

        while (true) {}


    }
}
