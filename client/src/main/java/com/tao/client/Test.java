package com.tao.client;

/**
 * Created by michael on 17-7-14.
 */
public class Test {

    public static void main(String[] args) {

        String userId = "hbtj121678";

        int workerIndex  = (userId.hashCode() & 0x7FFFFFFF) % 5;

        System.out.println("userId hashCode: " + userId.hashCode());
        System.out.println(workerIndex);
    }
}
