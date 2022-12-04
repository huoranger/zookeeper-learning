package life.huoranger;

/**
 * @author: 清风徐来
 * @date: 12/5/2022 2:15 AM
 * @description:
 */
public class LockTest {

    public static void main(String[] args) {

        Ticket12306 ticket12306 = new Ticket12306();

        Thread t1 = new Thread(ticket12306, "携程");
        Thread t2 = new Thread(ticket12306, "飞猪");

        t1.start();
        t2.start();
    }

}
