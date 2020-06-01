package io.stayhungrystayfoolish.redis.bloom;

import io.rebloom.client.Client;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/6/1 5:29 下午
 * @Description:
 * @Version: 1.0
 */
public class CuckooFilterDemo {


    public static void main(String[] args) {
        Client client = new Client("localhost", 6379);
        boolean add = client.add("k1", "v1");
        boolean result = client.exists("k1", "v1");
        System.out.println(add);
        System.out.println(result);
        boolean del = client.delete("k1");
        boolean delResult = client.exists("k1", "v1");
        System.out.println(del);
        System.out.println(delResult);
        client.close();

    }
}
