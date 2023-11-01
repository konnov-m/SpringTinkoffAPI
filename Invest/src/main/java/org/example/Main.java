package org.example;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("Hello world!");
            TimeUnit.SECONDS.sleep(1);
        }
    }
}