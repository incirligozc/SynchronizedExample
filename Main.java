package com.cihan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
    This example is created to understand the synchronizing.

    The story; there is a Producer thread and 2 Consumer threads. All of them are sharing the same object.
    Potential problems without synchronization:
        1- Producer adds a string to the buffer, consumer 1 might call isEmpty method and that returns false.
        So consumer1 is suspended and consumer2 removes a string then consumer2 suspended,
        consumer1 calls get(0), and that time there is no data in the buffer. Index out of bounds exception occurs.
        2- Both consumers can try to remove the same string.

    And so on.

    To prevent this errors; we use 'synchronized' keyword and lock the buffer in both threads(producer and consumer) so when a thread is using the 'buffer'
    other thread waits till the object is unlocked.

 */

public class Main {

    public static final String EOF = "EOF"; // end of file

    public static void main(String[] args) {
        List<String> buffer = new ArrayList<>();

        // creating the producer and consumer threads

        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_CYAN);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
    }
}

class MyProducer implements Runnable {

    private List<String> buffer;
    private String color;

    public MyProducer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public void run() {
        Random random = new Random();

        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num : nums) {
            try {
                System.out.println(color + "Adding.." + num);

                // when producer is filling the buffer, other threads can not use the same resource
                synchronized (buffer) {
                    buffer.add(num);
                }

                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted.");
            }
        }
        System.out.println(color + "Adding EOF and exiting...");
        synchronized (buffer) {
            // we add end of file string to terminate to program
            buffer.add(Main.EOF);
        }

    }
}

class MyConsumer implements Runnable {
    private List<String> buffer;
    private String color;

    public MyConsumer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public void run() {
        while (true) {
            // we lock the buffer object to prevent thread interference
            synchronized (buffer) {
                if (buffer.isEmpty()) {
                    continue;
                }
                if (buffer.get(0).equals(Main.EOF)) {
                    System.out.println(color + "Exiting");
                    break;
                } else {
                    // we remove the string from the buffer object.
                    System.out.println(color + "Removed " + buffer.remove(0));
                }
            }


        }
    }
}
