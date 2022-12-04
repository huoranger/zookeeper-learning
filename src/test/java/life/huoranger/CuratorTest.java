package life.huoranger;

import life.huoranger.constant.ZookeeperConstant;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: 清风徐来
 * @date: 12/4/2022 7:32 PM
 * @description:
 */
public class CuratorTest {

    private CuratorFramework client;


    /**
     * 建立链接
     */
    @Before
    public void testCreateConnection() {
        // 重试策略
        RetryPolicy retryPolice = new ExponentialBackoffRetry(3000, 10);
        // 1、方式1
//        CuratorFramework client =
//                CuratorFrameworkFactory.newClient(ZookeeperConstant.server, 60 * 1000, 15 * 1000, retryPolice);
        // 2、方式2
        client = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConstant.server)
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolice).namespace("huoranger").build();
        client.start();
    }

    /**
     * 创建节点
     */
    @Test
    public void testCreate() throws Exception {
        // 如果创建节点没有指定数据，则默认将当前客户端的ip作为指定数据存储
        System.out.println(client.create().withMode(CreateMode.PERSISTENT).forPath("/app1"));
    }

    /**
     * 查询数据
     */
    @Test
    public void testGetData() throws Exception {
        System.out.println(new String(client.getData().forPath("/app1")));
    }


    /**
     * 查询子节点
     */
    @Test
    public void testGetSubNode() throws Exception {
        List<String> path = client.getChildren().forPath("/");
        System.out.println(path);
    }

    /**
     * 查询节点的状态信息
     */
    @Test
    public void testGetNodeState() throws Exception {
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat);
    }

    /**
     * 修改数据
     */
    @Test
    public void testSet() throws Exception {
        client.setData().forPath("/app1", "huoranger".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 修改数据根据版本
     */
    @Test
    public void testSetForVersion() throws Exception {
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/app1");
        client.setData().withVersion(stat.getVersion()).forPath("/app1", "清风徐来".getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 删除节点
     */
    @Test
    public void deleteNode() throws Exception {
        // 删除节点
//        client.delete().forPath("/app1");
        // 必须成功删除: 为了防止网络抖动
//        client.delete().guaranteed().forPath("/app1");
        // 删除带有子节点的节点
//        client.delete().deletingChildrenIfNeeded().forPath("/app1");
        // 回调函数
        client.delete().guaranteed().inBackground((client, event) -> System.out.println("我被删除了~")).forPath("/app1");
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
}
